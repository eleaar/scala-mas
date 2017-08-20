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
import pl.edu.agh.scalamas.app.stream.graphs.{MeetingArenaFlow, SplitFlowByKey}
import pl.edu.agh.scalamas.mas.LogicStrategy
import pl.edu.agh.scalamas.mas.LogicTypes.Population
import pl.edu.agh.scalamas.random.RandomGeneratorComponent
import pl.edu.agh.scalamas.util.Util._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait SequentialStreamingStrategy
  extends StreamingLoopStrategy { this: LogicStrategy with ConcurrentAgentRuntimeComponent with
  RandomGeneratorComponent =>

  type Elem = Population

  private implicit val ec: ExecutionContext = agentRuntime.system.dispatcher

  protected final val initialSource: Source[Population, NotUsed] = Source.single(logic.initialPopulation)

  protected final val stepFlow: Flow[Population, Population, NotUsed] = {
    implicit val rand = randomData

    val meetingArenaFlow = MeetingArenaFlow(
      logic,
      timeout = 1.second,
      parallelism = 4
    ) _

    val subFlow = Flow[Elem]
      .mapConcat(x => x.shuffled)
      .via(
        SplitFlowByKey(
          logic.behaviourFunction,
          logic.behaviours.map(b => b -> meetingArenaFlow(b).async).toMap
        ))
      .fold[Elem](List.empty)(_.+:(_))

    Flow[Elem]
      .splitAfter(_ => true)
      .via(subFlow)
      .concatSubstreams
  }

}