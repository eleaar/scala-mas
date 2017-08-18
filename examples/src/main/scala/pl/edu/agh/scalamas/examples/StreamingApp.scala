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

package pl.edu.agh.scalamas.examples

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ClosedShape, KillSwitches}

import scala.concurrent.duration._

object StreamingApp {

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem("streamingEmas")
    implicit val ec = actorSystem.dispatcher
    implicit val materializer = ActorMaterializer()

    val log = Logging(actorSystem, getClass)

    val controlFlow = Flow[Int]
        .watchTermination()(Keep.right)
        .viaMat(KillSwitches.single)(Keep.both)

    val graph = RunnableGraph.fromGraph(
      GraphDSL.create(controlFlow) { implicit b => controlFlowH =>
        import GraphDSL.Implicits._

        val source = Source.single(0)
        val sink = Sink.foreach(println)
        val body = Flow[Int].map(_ + 1)

        val merge = b.add(MergePreferred[Int](1))
        val bcast = b.add(Broadcast[Int](2))

        source ~> merge ~> controlFlowH ~> body ~> bcast ~> sink
                  merge.preferred               <~ bcast

        ClosedShape
      }
    )

    log.info("Starting")
    val (graphTerminatedFuture, switch) = graph.run()
    log.info("Started")


    actorSystem.scheduler.scheduleOnce(5.seconds){
      log.info("Stopping")
      switch.shutdown()
    }
    for {
      _ <- graphTerminatedFuture
      _ <- actorSystem.terminate()
    } yield {
      log.info("Stopped")
    }

  }
}
