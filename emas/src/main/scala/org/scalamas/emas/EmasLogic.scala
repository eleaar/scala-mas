package org.scalamas.emas

import org.scalamas.emas.fight.DefaultFight
import org.scalamas.emas.reproduction.DefaultReproduction
import org.scalamas.genetic.GeneticProblem
import org.scalamas.mas.AgentRuntimeComponent
import org.scalamas.mas.logic.DelegatingLogicStrategy
import org.scalamas.mas.random.RandomGenerator
import org.scalamas.stats.StatsFactoryComponent

/**
 * Created by Daniel on 2015-01-14.
 */
trait EmasLogic extends DelegatingLogicStrategy
with EmasPopulation
with EmasBehaviour
with EmasMeetings with DefaultFight with DefaultReproduction
with EmasStats {

  // dependencies:
  this: AgentRuntimeComponent
    with GeneticProblem
    with StatsFactoryComponent
    with RandomGenerator =>
}
