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

package pl.edu.agh.scalamas.app.stream.graphs

import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl.{Flow, GraphDSL, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import org.apache.commons.math3.random.RandomDataGenerator
import pl.edu.agh.scalamas.util.RandomOrderedBuffer
import scala.concurrent.duration._

final class AnnealedShufflingBufferFlow[T] private (size: Int)(implicit random: RandomDataGenerator, ordering: Ordering[T]) extends GraphStage[FanInShape2[T, Double, T]] {
  require(size > 0, "size must be greater than 0")

  val in = Inlet[T]("in")
  val temperatureIn = Inlet[Double]("temperature")
  val out = Outlet[T]("out")

  val shape = new FanInShape2(in, temperatureIn, out)

  def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) with InHandler with OutHandler  {

    override def preStart(): Unit = {
      pull(in)
      pull(temperatureIn)
    }


    var temperature = 1.0
    setHandler(temperatureIn, new InHandler {
      def onPush(): Unit = {
        val value = grab(temperatureIn)
        temperature = math.min(1.0, math.max(0.0, value)) // constrain to [0, 1]
        pull(temperatureIn)
      }
    })

    val buffer = new RandomOrderedBuffer[T]()
    @inline private def notFull = buffer.size < size
    @inline private def isFull = !notFull

    private def flush(): Unit = {
      if(isFull) {
        val maybeElem = if(random.nextUniform(0.0, 1.0) < temperature) {
          buffer.removeRandom()
        } else {
          buffer.removeMax()
        }
        maybeElem.foreach(push(out, _))
      }
    }

    def onPush(): Unit = {
      buffer.add(grab(in))
      if(isAvailable(out)) flush()
      if(notFull) pull(in)
    }

    def onPull(): Unit = {
      flush()
      if (isClosed(in)) {
        if (buffer.isEmpty) completeStage()
      } else if (!hasBeenPulled(in)) {
        pull(in)
      }
    }

    override def onUpstreamFinish(): Unit = {
      if (buffer.isEmpty) completeStage()
    }

    setHandlers(in, out, this)

  }

}

object AnnealedShufflingBufferFlow {
  def apply[T](size: Int, halfDecayInSeconds: Int)(implicit random: RandomDataGenerator, ordering: Ordering[T]): Flow[T, T, NotUsed] = {
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val temperatureDecay = math.pow(2, -1.0 / halfDecayInSeconds)
      val temperature = Source.tick(initialDelay = 0.seconds, interval = 1.second, ())
          .scan(1.0) { case (currentTemperature, _) => currentTemperature * temperatureDecay}
      val flow = b.add(new AnnealedShufflingBufferFlow[T](size))

      temperature ~> flow.in1

      FlowShape(flow.in0, flow.out)
    })
  }
}
