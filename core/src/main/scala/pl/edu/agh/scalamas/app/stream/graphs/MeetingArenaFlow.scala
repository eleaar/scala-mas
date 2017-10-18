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
import akka.stream.scaladsl.Flow
import pl.edu.agh.scalamas.mas.Logic
import pl.edu.agh.scalamas.mas.LogicTypes.{Agent, Behaviour}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object MeetingArenaFlow {

  /**
   * Creates a flow for a specific behaviour.
   */
  def apply(logic: Logic, timeout: FiniteDuration, parallelism: Int)(behaviour: Behaviour)(implicit ec: ExecutionContext): Flow[Agent, Agent, NotUsed] = {
    Flow[Agent]
      .groupedWithin(behaviour.capacity, timeout)
      .mapAsync(parallelism) { agents =>
        Future(logic.meetingsFunction.apply((behaviour, agents.toList)))
      }
      .mapConcat(x => x)
  }

}
