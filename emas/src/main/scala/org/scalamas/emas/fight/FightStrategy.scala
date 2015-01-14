package org.scalamas.emas.fight

import org.scalamas.emas.EmasTypes.Agent
import org.scalamas.genetic.GeneticProblem

/**
 * Created by Daniel on 2015-01-14.
 */
trait FightStrategy {
  this: GeneticProblem =>

  def fightStrategy: Fight

  // TODO function instead of class
  trait Fight {
    def apply(agents: List[Agent[Genetic]]): List[Agent[Genetic]]
  }
}
