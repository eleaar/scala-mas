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

package pl.edu.agh.scalamas.genetic

import pl.edu.agh.scalamas.stats.{HasStats, Stats}

trait GeneticStats {
  this: GeneticProblem =>

  /**
   * Provides a stats implementation to accumulate the best evaluation value
   */
  def bestEvaluationStats: Stats[Genetic#Evaluation]

}

/**
 * Mixin for default genetic stats based on the evaluation ordering, provided there exists a stats factory for the type of evaluation.
 */
trait DefaultGeneticStats extends GeneticStats {
  this: GeneticProblem =>

  /**
   * Provides a stats implementation to accumulate values.
   */
  protected def hasStats: HasStats[Genetic#Evaluation]

  final lazy val bestEvaluationStats: Stats[Genetic#Evaluation] = {
    hasStats(
      initialValue = genetic.minimal,
      (a, b) => fixedMax(a, b)(genetic.ordering)
    )
  }

  /**
   * Hack because of scala bug SI-9087
   */
  private def fixedMax[T](x: T, y: T)(implicit ordering: Ordering[T]) = if (ordering.gt(x, y)) x else y

}
