package com.krzywicki.util

import com.krzywicki.util.Genetic._
import scala.math._
import com.krzywicki.util.Util._

object MAS {

  type Energy = Int

  case class Agent(val solution: Solution, val fitness: Fitness, var energy: Energy)

  type Population = List[Agent]
  type Group = (Behaviour, Population)
  type Meetings = PartialFunction[Group, Population]

  def createAgent(implicit config: EmasConfig): Agent = {
    val solution = createSolution
    Agent(solution, evaluate(solution), config.initialEnergy)
  }

  def createPopulation(implicit config: EmasConfig): Population = {
    List.fill(config.populationSize)(createAgent)
  }

  sealed trait Behaviour

  case object Death extends Behaviour

  case object Fight extends Behaviour

  case object Reproduction extends Behaviour

  case object Migration extends Behaviour

  def behaviour(agent: Agent)(implicit config: EmasConfig) =
    agent.energy match {
      case 0 => Death
      case _ if random < config.migrationProbability => Migration
      case energy if energy >= config.reproductionThreshold => Reproduction
      case _ => Fight
    }

  def meetings(implicit config: EmasConfig): Meetings = {
    case (Death, _) => List.empty[Agent]
    case (Fight, agents) =>
      agents.shuffled.grouped(2).flatMap(fight).toList
    case (Reproduction, agents) =>
      agents.shuffled.grouped(2).flatMap(reproduction).toList
    case (Migration, agents) => agents
  }


  def fight(agents: List[Agent])(implicit config: EmasConfig) = agents match {
    case List(a) => List(a)
    case List(a, b) =>
      val AtoBTransfer =
        if (a.fitness < b.fitness)
          min(config.fightTransfer, a.energy)
        else
          -min(config.fightTransfer, b.energy)
      List(a.copy(energy = a.energy - AtoBTransfer), b.copy(energy = b.energy + AtoBTransfer))
  }

  def reproduction(agents: List[Agent])(implicit config: EmasConfig) = agents match {
    case List(a) =>
      val s = reproduce(a.solution)
      val f = evaluate(s)
      val e = min(config.reproductionTransfer, a.energy)
      List(a.copy(energy = a.energy - e), Agent(s, f, e))
    case List(a1, a2) =>
      val (s1, s2) = reproduce(a1.solution, a2.solution)
      val (f1, f2) = (evaluate(s1), evaluate(s2))
      val (e1, e2) = (min(config.reproductionTransfer, a1.energy), min(config.reproductionTransfer, a2.energy))
      List(a1.copy(energy = a1.energy - e1), a2.copy(energy = a2.energy - e2), Agent(s1, f1, e1), Agent(s2, f2, e2))
  }

}