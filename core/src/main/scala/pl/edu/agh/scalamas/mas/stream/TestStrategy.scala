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

package pl.edu.agh.scalamas.mas.stream

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import pl.edu.agh.scalamas.app.ConcurrentAgentRuntimeComponent
import pl.edu.agh.scalamas.app.stream.StreamingLoopStrategy
import pl.edu.agh.scalamas.mas.LogicStrategy
import pl.edu.agh.scalamas.random.RandomGeneratorComponent

import scala.collection.immutable
import pl.edu.agh.scalamas.util.Util._

trait TestStrategy extends StreamingLoopStrategy { this: LogicStrategy with ConcurrentAgentRuntimeComponent with
  RandomGeneratorComponent =>

  type Elem = immutable.Seq[Int]


  protected final val initialSource: Source[Elem, NotUsed] = Source.single(1 to 10)

  protected final val stepFlow: Flow[Elem, Elem, NotUsed] = {
    implicit val rand = randomData

    Flow[Elem]
      .log("entering flow")
      .splitAfter(_ => true)
      .mapConcat(x => x.shuffled)
        .log("inside subflow")
        .fold[Elem](immutable.Seq.empty)(_.+:(_))
      .concatSubstreams
  }

}