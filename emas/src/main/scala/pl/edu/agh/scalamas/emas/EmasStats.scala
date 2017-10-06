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

package pl.edu.agh.scalamas.emas

import pl.edu.agh.scalamas.genetic.{GeneticProblem, GeneticStats}
import pl.edu.agh.scalamas.stats._

/**
 * Default EMAS statistics. Records the number of fitness evaluations performed so far and the best evaluation found so far.
 */
trait EmasStats extends StatsComponent {
  this: GeneticProblem with GeneticStats =>

  type StatsType = (Genetic#Evaluation, Long)

  lazy val stats: Stats[StatsType] = {
    val evaluationStats = bestEvaluationStats
    val reproductionCountStats = LongStats(0L, _ + _)

    TupleStats(
      evaluationStats,
      reproductionCountStats,
    )
  }

  def formatter = {
    case (fitness, reps) => s"$fitness $reps"
  }

}