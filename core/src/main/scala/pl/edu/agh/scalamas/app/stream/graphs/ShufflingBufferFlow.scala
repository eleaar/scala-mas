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
import akka.stream.Attributes.name
import akka.stream.scaladsl.Flow
import akka.stream.stage._
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import org.apache.commons.math3.random.RandomDataGenerator
import pl.edu.agh.scalamas.util.RandomBuffer

final class ShufflingBufferFlow[T] private (size: Int)(implicit random: RandomDataGenerator) extends GraphStage[FlowShape[T, T]] {
  require(size > 0, "size must be greater than 0")

  val in = Inlet[T]("in")
  val out = Outlet[T]("out")

  override val initialAttributes = name("shufflingBuffer")

  val shape = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) with InHandler with OutHandler {

    private val buffer = RandomBuffer[T]()
    @inline private def notFull = buffer.size < size
    @inline private def isFull = !notFull


    private def flush(): Unit = {
      if(isFull) {
        buffer.removeRandom().foreach { elem =>
          push(out, elem)
        }
      }
    }

    override def preStart(): Unit = pull(in)

    override def onUpstreamFinish(): Unit = {
      if (buffer.isEmpty) completeStage()
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

    setHandlers(in, out, this)
  }

}

object ShufflingBufferFlow {
  def apply[T](size: Int)(implicit random: RandomDataGenerator): Flow[T, T, NotUsed] =
    Flow.fromGraph(new ShufflingBufferFlow[T](size))
}
