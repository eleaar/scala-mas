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

package pl.edu.agh.scalamas.emas.stats

import akka.NotUsed
import akka.stream.scaladsl.Flow
import pl.edu.agh.scalamas.app.AgentRuntimeComponent
import pl.edu.agh.scalamas.app.stream.StreamingLoopStrategy
import pl.edu.agh.scalamas.emas.EmasTypes
import pl.edu.agh.scalamas.mas.LogicTypes.Population

trait EmasStreamingIterationStats extends AgentIterationStats with StreamingLoopStrategy {
  this: AgentRuntimeComponent =>

  override type Elem = Population

  abstract override protected def stepFlow: Flow[Elem, Elem, NotUsed] = {
    import agentIterationStats._
    if(enabled) {
      Flow.fromFunction[Elem, Elem] { population =>
        population.map {
          case a: EmasTypes.Agent[_] =>
            timedIterationHistogram += a.iteration
            a.copy(iteration = a.iteration + 1)
          case x => x
        }
      } via super.stepFlow
    } else {
      super.stepFlow
    }
  }

}
