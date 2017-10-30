/*
 * Copyright (c) 2013 Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pl.edu.agh.scalamas.emas.stream

import akka.NotUsed
import akka.stream.javadsl.Zip
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink}
import akka.stream.{FlowShape, OverflowStrategy, ThrottleMode}
import net.ceedubs.ficus.Ficus._
import pl.edu.agh.scalamas.app.AgentRuntimeComponent
import pl.edu.agh.scalamas.emas.EmasTypes
import pl.edu.agh.scalamas.genetic.GeneticProblem
import pl.edu.agh.scalamas.mas.LogicTypes.Agent
import pl.edu.agh.scalamas.mas.stream.buffer.AgentBufferStrategy

import scala.concurrent.duration._

trait EmergencyLoggingStream extends AgentBufferStrategy {
  this: GeneticProblem with AgentRuntimeComponent =>

  import EmergencyLoggingStream._

  override abstract protected def agentBufferFlow = {
    val isActive = agentRuntime.config.as[Boolean]("streaming.emergencyLogging.active")

    if(isActive) {
      val warmup = agentRuntime.config.as[FiniteDuration]("streaming.emergencyLogging.warmup")
      val improvementMemory = agentRuntime.config.as[FiniteDuration](
        "streaming.emergencyLogging.take-snapshot-when-no-improvement-in"
      )
      val snapshotSize = agentRuntime.config.as[Int]("streaming.emergencyLogging.snapshot-size")
      val snapshotWindow = agentRuntime.config.as[FiniteDuration]("streaming.emergencyLogging.snapshot-window")

      val extractAgents: Flow[Agent, EmasTypes.Agent[Genetic], NotUsed] = Flow[Agent].collect {
        case agent: EmasTypes.Agent[Genetic] => agent
      }

      val noImprovement = Flow[EmasTypes.Agent[Genetic]]
        .map(_.fitness)
        .conflate(genetic.ordering.max)
        .expand(Iterator.continually(_)) // decouple rates and make sure we always have a best fitness so far
        .throttle(
        elements = 1,
        per = 1.second,
        maximumBurst = 0,
        mode = ThrottleMode.Shaping
      ) // measure the best fitness within the last second
        .drop(warmup.toSeconds.toInt) // give some time to warm up
        .sliding(improvementMemory.toSeconds.toInt).filterNot { values =>
        genetic.ordering.gt(values.last, values.head)
      }.map(_ => ()) // we emit a value whenever there has been no improvement in the last 5 seconds
        .throttle(
        elements = 1,
        per = improvementMemory,
        maximumBurst = 0,
        mode = ThrottleMode.Shaping
      )

      val agentsSnapshot = Flow[EmasTypes.Agent[Genetic]]
        .groupedWithin(snapshotSize, snapshotWindow)
        .buffer(1, OverflowStrategy.dropHead)

      val snapshotWhenNoImprovement = extractAgents via Flow.fromGraph(
        GraphDSL.create() { implicit b =>
          import GraphDSL.Implicits._

          val bcast = b.add(Broadcast[EmasTypes.Agent[Genetic]](2))
          val zip = b.add(Zip.create[Unit, Seq[EmasTypes.Agent[Genetic]]])

          bcast ~> noImprovement ~> zip.in0
          bcast ~> agentsSnapshot ~> zip.in1

          FlowShape(bcast.in, zip.out)
        })


      val probe = snapshotWhenNoImprovement.map(_.second).to(
        Sink.foreach { snapshot =>
          println(
            s"#>> Warning, no improvement since $improvementMemory. Took a snapshot of agents. " +
              s"There are ${snapshot.size} agents in the snapshot with total energy ${snapshot.map(_.energy).sum}: \n${snapshot.mkString(",")}")

        })
      probeWith(probe).via(super.agentBufferFlow)
    } else {
      super.agentBufferFlow
    }
  }
}

object EmergencyLoggingStream {

  def probeWith[A](probe: Sink[A, NotUsed]): Flow[A, A, NotUsed] = {
    Flow.fromGraph(
      GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._

        val bcast = b.add(Broadcast[A](2))
        val output = b.add(Flow[A])

        bcast.buffer(50, OverflowStrategy.dropHead) ~> probe
        bcast ~> output

        FlowShape(bcast.in, output.out)
      })
  }

}