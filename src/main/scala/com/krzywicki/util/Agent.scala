package com.krzywicki.util

import com.krzywicki.util.Genetic._

object Agent {

  type Energy = Int
  case class Agent(solution: Solution, fitness: Fitness, energy: Energy)
  type Population = List[Agent]

  def createAgent(implicit config: Config): Agent = {
    val solution = createSolution
    Agent(solution, evaluateSolution(solution), config.initialEnergy)
  }

  def createPopulation(implicit config: Config): Population = {
    List.fill(config.populationSize)(createAgent)
  }

  trait Behaviour
  case object Death extends Behaviour
  case object Fight extends Behaviour
  case object Reproduction extends Behaviour

  def behaviour(agent: Agent)(implicit config: Config) =
    agent match {
      case Agent(_, _, 0) => Death
      case Agent(_, _, energy) if energy > config.reproductionThreshold => Reproduction
      case _ => Fight
    }

//  def fight(a1: Agent, a2: Agent) {
//    val (winner, loser) = if (a1.fitness > a2.fitness) (a1, a2) else (a2, a1)
//    
//  }

}