package org.scalamas.emas.reproduction

import org.scalamas.emas.EmasTypes.Agent
import org.scalamas.genetic.GeneticProblem

/**
 * Created by Daniel on 2015-01-14.
 */
trait ReproductionStrategy {
  this: GeneticProblem =>

  def reproductionStrategy: Reproduction

  // TODO function instead of class
  trait Reproduction {
    def apply(agents: List[Agent[Genetic]]): List[Agent[Genetic]]
  }

}
