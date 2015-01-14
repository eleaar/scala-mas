package org.scalamas.emas

import org.scalamas.emas.fight.FightStrategy
import org.scalamas.emas.reproduction.ReproductionStrategy
import org.scalamas.genetic.GeneticProblem
import org.scalamas.mas.LogicTypes.Migration
import org.scalamas.mas.logic.MeetingsStrategy
import org.scalamas.util.Util._

/**
 * Created by Daniel on 2015-01-14.
 */
trait EmasMeetings extends MeetingsStrategy {
  this: GeneticProblem
    with EmasStats
    with FightStrategy
    with ReproductionStrategy =>

  def meetingsStrategy = DefaultEmasMeeting

  object DefaultEmasMeeting extends MeetingsProvider {

    import org.scalamas.emas.EmasTypes._

    implicit val ordering = genetic.ordering

    // TODO fix shuffle bottleneck
    def meetingsFunction = {
      case (Death(_), _) => List.empty[Agent[Genetic]]
      case (Fight(cap), agents) =>
        checked[Genetic](agents).shuffled.grouped(cap).flatMap(fightStrategy.apply).toList
      case (Reproduction(cap), agents) =>
        val newAgents = checked[Genetic](agents).shuffled.grouped(cap).flatMap(reproductionStrategy.apply).toList
        stats.update((newAgents.maxBy(_.fitness).fitness, agents.size))
        newAgents
      case (Migration(_), agents) => agents
    }
  }

}
