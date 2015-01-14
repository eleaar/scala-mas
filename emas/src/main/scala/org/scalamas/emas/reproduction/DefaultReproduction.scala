package org.scalamas.emas.reproduction

import org.scalamas.genetic.GeneticProblem
import org.scalamas.mas.AgentRuntimeComponent

import scala.math._

/**
 * Created by Daniel on 2015-01-14.
 */
trait DefaultReproduction extends ReproductionStrategy {
  this: AgentRuntimeComponent with GeneticProblem =>

  def reproductionStrategy = DefaultReproductionImpl

  object DefaultReproductionImpl extends Reproduction {

    import org.scalamas.emas.EmasTypes.Agent

    val config = agentRuntime.config.getConfig("emas")
    val reproductionTransfer = config.getInt("reproductionTransfer")

    def apply(agents: List[Agent[Genetic]]) = agents match {
      case List(a) =>
        val s = genetic.transform(a.solution)
        val f = genetic.evaluate(s)
        val e = min(reproductionTransfer, a.energy)
        List(a.copy(energy = a.energy - e), Agent[Genetic](s, f, e))
      case List(a1, a2) =>
        val (s1, s2) = genetic.transform(a1.solution, a2.solution)
        val (f1, f2) = (genetic.evaluate(s1), genetic.evaluate(s2))
        val (e1, e2) = (min(reproductionTransfer, a1.energy), min(reproductionTransfer, a2.energy))
        List(a1.copy(energy = a1.energy - e1), a2.copy(energy = a2.energy - e2), Agent[Genetic](s1, f1, e1), Agent[Genetic](s2, f2, e2))
    }
  }

}

