package org.scalamas.emas.fight

import org.scalamas.app.AgentRuntimeComponent
import org.scalamas.genetic.GeneticProblem

import scala.math._

/**
 * Created by Daniel on 2015-01-14.
 */
trait DefaultFight extends FightStrategy {
  this: AgentRuntimeComponent with GeneticProblem =>

  def fightStrategy = DefaultFightImpl

  object DefaultFightImpl extends Fight {

    import org.scalamas.emas.EmasTypes._

    val config = agentRuntime.config.getConfig("emas")
    val fightTransfer = config.getInt("fightTransfer")

    def apply(agents: List[Agent[Genetic]]) = agents match {
      case List(a) => List(a)
      case List(a, b) =>
        val AtoBTransfer =
          if (genetic.ordering.lt(a.fitness, b.fitness))
            min(fightTransfer, a.energy)
          else
            -min(fightTransfer, b.energy)
        List(a.copy(energy = a.energy - AtoBTransfer), b.copy(energy = b.energy + AtoBTransfer))
    }
  }

}
