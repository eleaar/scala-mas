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

import akka.{Done, NotUsed}
import akka.stream.{ClosedShape, KillSwitches, OverflowStrategy, UniqueKillSwitch}
import akka.stream.scaladsl.{Broadcast, Concat, Flow, GraphDSL, Keep, MergePreferred, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object LoopingGraph {
  def apply[T](source: Source[T, NotUsed], flow: Flow[T, T, NotUsed], bufferSize: Int): RunnableGraph[(UniqueKillSwitch, Future[Done])] = {
    val switch = Flow.fromGraph(KillSwitches.single[T])
      .watchTermination()(Keep.both)

    RunnableGraph.fromGraph(
      GraphDSL.create(switch) { implicit b => switchH =>
        import GraphDSL.Implicits._

        val concat = b.add(Concat[T](2))
        val bcast = b.add(Broadcast[T](2))
        val buffer = Flow[T].buffer(size = bufferSize, overflowStrategy = OverflowStrategy.fail)

        source ~> concat ~> switchH ~> flow ~> bcast ~> Sink.ignore
        concat <~ buffer <~ bcast

        ClosedShape
      }
    )
  }
}
