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
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import org.apache.commons.math3.random.RandomDataGenerator
import pl.edu.agh.scalamas.util.Util._

import scala.collection.mutable.ArrayBuffer

class BarrierBufferFlow[T](shouldFlushAfter: T => Boolean)(implicit random: RandomDataGenerator)
  extends GraphStage[FlowShape[T, T]] {

  val in = Inlet[T]("in")

  val out = Outlet[T]("out")

  override val initialAttributes = name("barrierBuffer")

  val shape = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) with
      InHandler with OutHandler {

      private var i = 0
      private var buffer = ArrayBuffer[T]()
      private var isFlushing = false

      override def preStart(): Unit = pull(in)

      def onPush(): Unit = {
        val elem = grab(in)
        buffer += elem
        if (shouldFlushAfter(elem)) {
          startFlushing()
        } else {
          pull(in)
        }
      }

      private def startFlushing(): Unit = {
        assert(buffer.nonEmpty)
        buffer = buffer.shuffled
        i = 0
        isFlushing = true
        if (isAvailable(out)) {
          onPull()
        }
      }

      def onPull(): Unit = {
        if (isFlushing) {
          // when flushing, the buffer contains at least one element
          push(out, buffer(i))
          i += 1

          if (i == buffer.size) {
            stopFlushing()
          }
        }
      }

      private def stopFlushing(): Unit = {
        buffer.clear()
        isFlushing = false

        if (isClosed(in)) {
          completeStage()
        } else {
          pull(in)
        }
      }

      override def onUpstreamFinish(): Unit = {
        if (!isFlushing) completeStage()
      }

      setHandlers(in, out, this)
    }
  }


}

object BarrierBufferFlow {
  def apply[T](shouldFlushAfter: T => Boolean)(implicit random: RandomDataGenerator): Flow[T, T, NotUsed] = {
    Flow.fromGraph(new BarrierBufferFlow[T](shouldFlushAfter))
  }
}
