/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.edu.agh.scalamas.emas

import pl.edu.agh.scalamas.genetic.GeneticProblem
import pl.edu.agh.scalamas.stats.{Stats, StatsComponent, StatsFactoryComponent}

/**
 * Created by Daniel on 2015-01-14.
 */
trait EmasStats extends StatsComponent {
  this: GeneticProblem with StatsFactoryComponent =>

  type StatsType = (Genetic#Evaluation, Long)

  lazy val stats: Stats[StatsType] = statsFactory((genetic.minimal, 0L)) {
    case ((oldFitness, oldReps), (newFitness, newReps)) =>
      (fixedMax(oldFitness, newFitness)(genetic.ordering), oldReps + newReps)
  }

  def formatter = {
    case (fitness, reps) => s"$fitness $reps"
  }

  /**
   * Hack because of scala bug SI-9087
   */
  private[this] def fixedMax[T](x: T, y: T)(implicit ordering: Ordering[T]) = if (ordering.gt(x, y)) x else y
}