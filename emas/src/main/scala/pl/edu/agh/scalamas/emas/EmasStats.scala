package org.scalamas.emas

import org.scalamas.genetic.GeneticProblem
import org.scalamas.stats.{Stats, StatsComponent, StatsFactoryComponent}

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
  private def fixedMax[T](x: T, y: T)(implicit ordering: Ordering[T]) = if (ordering.gt(x, y)) x else y
}
