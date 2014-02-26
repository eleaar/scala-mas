package com.krzywicki.util

import com.krzywicki.util.Genetic._
import scala.math._
import com.krzywicki.util.Util._

object MAS {

  type Energy = Int
  class Agent(val solution: Solution, val fitness: Fitness, var energy: Energy)
  type Population = List[Agent]

  def createAgent(implicit config: Config): Agent = {
    val solution = createSolution
    new Agent(solution, evaluate(solution), config.initialEnergy)
  }

  def createPopulation(implicit config: Config): Population = {
    List.fill(config.populationSize)(createAgent)
  }

  trait Behaviour
  case object Death extends Behaviour
  case object Fight extends Behaviour
  case object Reproduction extends Behaviour
  case object Migration extends Behaviour

  def behaviour(agent: Agent)(implicit config: Config) =
    agent.energy match {
      case 0 => Death
      case _ if random < config.migrationProbability => Migration
      case energy if energy >= config.reproductionThreshold => Reproduction
      case _ => Fight
    }

  def meetings(implicit config: Config): PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Death, _) => List.empty[Agent]
    case (Fight, agents) =>
      agents.shuffled.grouped(2).flatMap(fight).toList
    case (Reproduction, agents) =>
      agents.shuffled.grouped(2).flatMap(reproduction).toList
    case (Migration, agents) => agents
  }

  def fight(agents: List[Agent])(implicit config: Config) = agents match {
    case List(a) => List(a)
    case List(a1, a2) =>
      val AtoBTransfer =
        if (a1.fitness < a2.fitness)
          min(config.fightTransfer, a1.energy)
        else
          -min(config.fightTransfer, a2.energy)
      a1.energy -= AtoBTransfer
      a2.energy += AtoBTransfer
      List(a1, a2)
  }

  def reproduction(agents: List[Agent])(implicit config: Config) = agents match {
    case List(a) =>
      val s = reproduce(a.solution)
      val f = evaluate(s)
      val e = min(config.reproductionTransfer, a.energy)
      a.energy -= e;
      List(a, new Agent(s, f, e))
    case List(a1, a2) =>
      val (s1, s2) = reproduce(a1.solution, a2.solution)
      val (f1, f2) = (evaluate(s1), evaluate(s2))
      val (e1, e2) = (min(config.reproductionTransfer, a1.energy), min(config.reproductionTransfer, a2.energy))
      a1.energy -= e1;
      a2.energy -= e2;
      List(a1, a2, new Agent(s1, f1, e1), new Agent(s2, f2, e2))
  }

}