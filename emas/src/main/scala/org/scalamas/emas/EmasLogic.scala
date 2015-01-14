/**
 * Copyright (C) 2013 - 2014, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * This file is part of ParaphraseAGH/Scala.
 *
 * ParaphraseAGH/Scala is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ParaphraseAGH/Scala is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ParaphraseAGH/Scala.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.scalamas.emas

import org.scalamas.genetic.{GeneticProblem, GeneticOps}

import scala.math._
import org.scalamas.mas.util.Util._
import org.scalamas.mas.{AgentRuntimeComponent, LogicComponent, LogicTypes, Logic}
import org.scalamas.mas.LogicTypes._
import org.scalamas.stats.Stats

object EmasLogic {
  case class Agent[G <: GeneticOps[G]](val solution: G#Solution, val fitness: G#Evaluation, var energy: Int) extends LogicTypes.Agent
  case class Death(capacity: Int) extends Behaviour
  case class Fight(capacity: Int) extends Behaviour
  case class Reproduction(capacity: Int) extends Behaviour

  def checked[G <: GeneticOps[G]](pop: Population) = pop.collect{ case a: EmasLogic.Agent[G] => a}
}

trait EmasLogicComponent extends LogicComponent {
  this: AgentRuntimeComponent with GeneticProblem with EmasStats with EmasReproductionComponent =>

  def config = agentRuntime.config.getConfig("emas")

  lazy val populationSize = config.getInt("populationSize")
  lazy val initialEnergy = config.getInt("initialEnergy")
  lazy val reproductionThreshold = config.getInt("reproductionThreshold")
  lazy val reproductionTransfer = config.getInt("reproductionTransfer")
  lazy val fightTransfer = config.getInt("fightTransfer")
  lazy val migrationProbability = config.getDouble("migrationProbability")

  lazy val fightCapacity = config.getInt("fightCapacity")
  lazy val reproductionCapacity = config.getInt("reproductionCapacity")
  lazy val migrationCapacity = config.getInt("migrationCapacity")
  lazy val deathCapacity = config.getInt("deathCapacity")

  def logic = new EmasLogic

  class EmasLogic extends Logic {

    import EmasLogic._

    implicit val ordering = genetic.ordering

    // TODO use random component
    def random = math.random

    def initialPopulation: Population = {
      val population = List.fill(populationSize) {
        val solution = genetic.generate
        Agent[Genetic](solution, genetic.evaluate(solution), initialEnergy)
      }
      population.maxBy(_.fitness).fitness
      stats.update((population.maxBy(_.fitness).fitness, 0L))
      population
    }


    val death = Death(deathCapacity)
    val fight = Fight(fightCapacity)
    val reproduce = Reproduction(reproductionCapacity)
    val migrate = Migration(migrationCapacity)

    val behaviours = List(death, fight, reproduce, migrate)

    def behaviourFunction = {
      case Agent(_, _, energy) => energy match {
        case 0 => death
        case _ if random < migrationProbability => migrate
        case energy if energy >= reproductionThreshold => reproduce
        case _ => fight
      }
    }

    // TODO fix shuffle bottleneck
    def meetingsFunction = {
      case (Death(_), _) => List.empty[Agent[Genetic]]
      case (Fight(cap), agents) =>
        checked[Genetic](agents).shuffled.grouped(cap).flatMap(doFight).toList
      case (Reproduction(cap), agents) =>
        val newAgents = checked[Genetic](agents).shuffled.grouped(cap).flatMap(reproduction).toList
        //        println(newAgents.maxBy(_.fitness).fitness)
        stats.update((newAgents.maxBy(_.fitness).fitness, agents.size))
        newAgents
      case (Migration(_), agents) => agents
    }

    private def doFight(agents: List[Agent[Genetic]]): List[Agent[Genetic]] = agents match {
      case List(a) => List(a)
      case List(a, b) =>
        val AtoBTransfer =
          if (ordering.lt(a.fitness, b.fitness))
            min(fightTransfer, a.energy)
          else
            -min(fightTransfer, b.energy)
        List(a.copy(energy = a.energy - AtoBTransfer), b.copy(energy = b.energy + AtoBTransfer))
    }
  }

}

trait EmasReproductionComponent {
  this: GeneticProblem =>

  import EmasLogic.Agent

  def reproductionTransfer: Int

  def reproduction(agents: List[Agent[Genetic]]): List[Agent[Genetic]] = agents match {
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