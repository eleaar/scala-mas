package org.scalamas.genetic


/**
 * Created by Daniel on 2015-01-12.
 */
trait GeneticProblem {

  type Genetic <: GeneticOps[Genetic]

  def genetic: Genetic
}
