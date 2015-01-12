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

package org.paramas.emas

import org.scalamas.genetic.GeneticOps

import scala.math._
import org.paramas.mas.util.Util._
import org.paramas.emas.config.AppConfig
import org.paramas.mas.{LogicTypes, Logic}
import org.paramas.mas.LogicTypes._
import org.paramas.stats.Stats

object EmasLogic {
  case class Agent[G <: GeneticOps[G]](val solution: G#Solution, val fitness: G#Evaluation, var energy: Int) extends LogicTypes.Agent
  case class Death(capacity: Int) extends Behaviour
  case class Fight(capacity: Int) extends Behaviour
  case class Reproduction(capacity: Int) extends Behaviour

  def checked[G <: GeneticOps[G]](pop: Population) = pop.collect{ case a: EmasLogic.Agent[G] => a}
}

class EmasLogic[G <: GeneticOps[G]](val ops: G, val stats: Stats[(G#Evaluation, Long)], implicit val config: AppConfig) extends Logic {

  import ops._
  import org.paramas.emas.EmasLogic._

  implicit val ordering = ops.ordering

  def random = math.random

  def initialPopulation: Population = {
    val population = List.fill(config.emas.populationSize) {
      val solution = generate
      Agent[G](solution, evaluate(solution), config.emas.initialEnergy)
    }
    population.maxBy(_.fitness).fitness
    stats.update((population.maxBy(_.fitness).fitness, 0L))
    population
  }


  val death = Death(config.emas.deathCapacity)
  val fight = Fight(config.emas.fightCapacity)
  val reproduce = Reproduction(config.emas.reproductionCapacity)
  val migrate = Migration(config.emas.migrationCapacity)

  val behaviours = List(death, fight, reproduce, migrate)

  def behaviourFunction = {
    case Agent(_,_,energy) => energy match {
      case 0 => death
      case _ if random < config.emas.migrationProbability => migrate
      case energy if energy >= config.emas.reproductionThreshold => reproduce
      case _ => fight
    }
  }

  def meetingsFunction = {
    case (Death(_), _) => List.empty[Agent[G]]
    case (Fight(cap), agents) =>
      checked[G](agents).shuffled.grouped(cap).flatMap(doFight).toList
    case (Reproduction(cap), agents) =>
      val newAgents = checked[G](agents).shuffled.grouped(cap).flatMap(doReproduce).toList
      newAgents.maxBy(_.fitness).fitness
      stats.update((newAgents.maxBy(_.fitness).fitness, agents.size))
      newAgents
    case (Migration(_), agents) => agents
  }

  private def doFight(agents: List[Agent[G]])(implicit config: AppConfig): List[Agent[G]] = agents match {
    case List(a) => List(a)
    case List(a, b) =>
      val AtoBTransfer =
        if (ordering.lt(a.fitness, b.fitness))
          min(config.emas.fightTransfer, a.energy)
        else
          -min(config.emas.fightTransfer, b.energy)
      List(a.copy(energy = a.energy - AtoBTransfer), b.copy(energy = b.energy + AtoBTransfer))
  }

  private def doReproduce(agents: List[Agent[G]])(implicit config: AppConfig): List[Agent[G]] = agents match {
    case List(a) =>
      val s = transform(a.solution)
      val f = evaluate(s)
      val e = min(config.emas.reproductionTransfer, a.energy)
      List(a.copy(energy = a.energy - e), Agent[G](s, f, e))
    case List(a1, a2) =>
      val (s1, s2) = transform(a1.solution, a2.solution)
      val (f1, f2) = (evaluate(s1), evaluate(s2))
      val (e1, e2) = (min(config.emas.reproductionTransfer, a1.energy), min(config.emas.reproductionTransfer, a2.energy))
      List(a1.copy(energy = a1.energy - e1), a2.copy(energy = a2.energy - e2), Agent[G](s1, f1, e1), Agent[G](s2, f2, e2))
  }

}