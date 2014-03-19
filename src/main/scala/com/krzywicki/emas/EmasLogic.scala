package com.krzywicki.emas

import com.krzywicki.util.Genetic._
import scala.math._
import com.krzywicki.util.Util._
import com.krzywicki.config.AppConfig
import com.krzywicki.mas.{LogicTypes, Logic}
import com.krzywicki.mas.LogicTypes._

object EmasLogic {
  case class Agent(val solution: Solution, val fitness: Fitness, var energy: Int) extends LogicTypes.Agent
  case object Death extends Behaviour
  case object Fight extends Behaviour
  case object Reproduction extends Behaviour

  def checked(pop: Population) = pop.collect{ case a: EmasLogic.Agent => a}
}

class EmasLogic(implicit val config: AppConfig) extends Logic {
 import EmasLogic._


  def initialPopulation: Population =
    List.fill(config.emas.populationSize) {
      val solution = createSolution
      Agent(solution, evaluate(solution), config.emas.initialEnergy)
    }

  val behaviours = List(Death, Fight, Reproduction, Migration)

  def behaviourFunction = {
    case Agent(_,_,energy) => energy match {
      case 0 => Death
      case _ if random < config.emas.migrationProbability => Migration
      case energy if energy >= config.emas.reproductionThreshold => Reproduction
      case _ => Fight
    }
  }

  def meetingsFunction = {
    case (Death, _) => List.empty[Agent]
    case (Fight, agents) =>
      checked(agents).shuffled.grouped(2).flatMap(fight).toList
    case (Reproduction, agents) =>
      checked(agents).shuffled.grouped(2).flatMap(reproduction).toList
    case (Migration, agents) => agents
  }

  private def fight(agents: List[Agent])(implicit config: AppConfig) = agents match {
    case List(a) => List(a)
    case List(a, b) =>
      val AtoBTransfer =
        if (a.fitness < b.fitness)
          min(config.emas.fightTransfer, a.energy)
        else
          -min(config.emas.fightTransfer, b.energy)
      List(a.copy(energy = a.energy - AtoBTransfer), b.copy(energy = b.energy + AtoBTransfer))
  }

  private def reproduction(agents: List[Agent])(implicit config: AppConfig) = agents match {
    case List(a) =>
      val s = reproduce(a.solution)
      val f = evaluate(s)
      val e = min(config.emas.reproductionTransfer, a.energy)
      List(a.copy(energy = a.energy - e), Agent(s, f, e))
    case List(a1, a2) =>
      val (s1, s2) = reproduce(a1.solution, a2.solution)
      val (f1, f2) = (evaluate(s1), evaluate(s2))
      val (e1, e2) = (min(config.emas.reproductionTransfer, a1.energy), min(config.emas.reproductionTransfer, a2.energy))
      List(a1.copy(energy = a1.energy - e1), a2.copy(energy = a2.energy - e2), Agent(s1, f1, e1), Agent(s2, f2, e2))
  }

}