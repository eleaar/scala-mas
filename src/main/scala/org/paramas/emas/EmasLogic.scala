package org.paramas.emas

import Genetic._
import scala.math._
import org.paramas.mas.util.Util._
import org.paramas.emas.config.AppConfig
import org.paramas.mas.{Stats, LogicTypes, Logic}
import org.paramas.mas.LogicTypes._

object EmasLogic {
  case class Agent(val solution: Solution, val fitness: Fitness, var energy: Int) extends LogicTypes.Agent
  case class Death(capacity: Int) extends Behaviour
  case class Fight(capacity: Int) extends Behaviour
  case class Reproduction(capacity: Int) extends Behaviour

  def checked(pop: Population) = pop.collect{ case a: EmasLogic.Agent => a}
}

class EmasLogic(implicit val stats: Stats[(Double, Long)], implicit val config: AppConfig) extends Logic {
 import EmasLogic._


  def initialPopulation: Population = {
    val population = List.fill(config.emas.populationSize) {
      val solution = createSolution
      Agent(solution, evaluate(solution), config.emas.initialEnergy)
    }
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
    case (Death(_), _) => List.empty[Agent]
    case (Fight(cap), agents) =>
      checked(agents).shuffled.grouped(cap).flatMap(doFight).toList
    case (Reproduction(cap), agents) =>
      val newAgents = checked(agents).shuffled.grouped(cap).flatMap(doReproduce).toList
      stats.update(newAgents.maxBy(_.fitness).fitness, agents.size)
      newAgents
    case (Migration(_), agents) => agents
  }

  private def doFight(agents: List[Agent])(implicit config: AppConfig) = agents match {
    case List(a) => List(a)
    case List(a, b) =>
      val AtoBTransfer =
        if (a.fitness < b.fitness)
          min(config.emas.fightTransfer, a.energy)
        else
          -min(config.emas.fightTransfer, b.energy)
      List(a.copy(energy = a.energy - AtoBTransfer), b.copy(energy = b.energy + AtoBTransfer))
  }

  private def doReproduce(agents: List[Agent])(implicit config: AppConfig) = agents match {
    case List(a) =>
      val s = transform(a.solution)
      val f = evaluate(s)
      val e = min(config.emas.reproductionTransfer, a.energy)
      List(a.copy(energy = a.energy - e), Agent(s, f, e))
    case List(a1, a2) =>
      val (s1, s2) = transform(a1.solution, a2.solution)
      val (f1, f2) = (evaluate(s1), evaluate(s2))
      val (e1, e2) = (min(config.emas.reproductionTransfer, a1.energy), min(config.emas.reproductionTransfer, a2.energy))
      List(a1.copy(energy = a1.energy - e1), a2.copy(energy = a2.energy - e2), Agent(s1, f1, e1), Agent(s2, f2, e2))
  }

}