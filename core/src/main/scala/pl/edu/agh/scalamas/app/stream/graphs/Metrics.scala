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
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Unzip, Zip}
import com.codahale.metrics.Timer
import nl.grons.metrics.scala.DefaultInstrumented

import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable

object Metrics extends DefaultInstrumented {

  def histogram[T](name: String)(f: T => Int): Flow[T, T, NotUsed] = {
    val histogram = metrics.histogram("scalamas.sequential.populationSize")
    Flow.fromFunction[T, T] { t =>
      histogram += f(t)
      t
    }
  }

  /**
   * Creates a flow which updates a meter with a given name. The meter is shared across materialisations.
   */
  def meter[T](name: String): Flow[T, T, NotUsed] = {
    val meter = metrics.meter(name)
    Flow[T].map { t =>
      meter.mark()
      t
    }.named(name)
  }

  /**
   * Creates a flow which updates a timer for each element which passes through the provided flo.
   */
  // NOT WORKING YET
  def timer[A, B, Mat](name: String)(flow: Flow[A, B, _]): Flow[A, B, NotUsed] = {
    val timer = metrics.timer(name)

    // there can be any number of output elements for an input element. We correlate them by computing them in a substream
    val aggregatingFlow = Flow[A]
      .splitAfter(_ => true)
        .via(flow)
        .fold[Seq[B]](ArrayBuffer.empty)(_ :+ _)
      .concatSubstreams
      .async

    Flow.fromGraph(GraphDSL.create(aggregatingFlow) { implicit builder => flowH =>
      import GraphDSL.Implicits._

      val startContext = builder.add(Flow[A].map(a => (a, timer.timerContext())))
      val unzip = builder.add(Unzip[A, Timer.Context])
      val rezip = builder.add(Zip[Seq[B], Timer.Context])
      val stopContext = builder.add(Flow[(Seq[B], Timer.Context)].mapConcat {
        case (bs, ctx) =>
          ctx.stop()
          bs.to[immutable.Seq]
      })

      startContext ~> unzip.in
                      unzip.out0 ~> flowH ~> rezip.in0
                      unzip.out1          ~> rezip.in1
                                             rezip.out ~> stopContext

      FlowShape(startContext.in, stopContext.out)
    }).named(name)
  }


}
