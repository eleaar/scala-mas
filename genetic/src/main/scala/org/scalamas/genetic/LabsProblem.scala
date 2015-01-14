package org.scalamas.genetic

import org.scalamas.mas.AgentRuntimeComponent
import org.scalamas.mas.random.RandomGenerator

/**
 * An implementation of genetic operators for finding the maximu of the Labs function.
 */

trait LabsProblem extends GeneticProblem {
  this: AgentRuntimeComponent with RandomGenerator =>

  type Genetic = LabsOps

  def genetic = new LabsOps with SteepestDescend {
    def config = agentRuntime.config.getConfig("genetic.labs")

    val problemSize = config.getInt("problemSize")
    val mutationChance = config.getDouble("mutationChance")
    val mutationRate = config.getDouble("mutationRate")

    def random = LabsProblem.this.random
  }
}

trait LabsOps extends GeneticOps[LabsOps] with LocalSearch[LabsOps] {
  type Feature = Boolean
  type Solution = Array[Feature]
  type Evaluation = Double

  def random: Double

  def problemSize: Int

  def mutationChance: Double

  def mutationRate: Double

  def generate = Array.fill(problemSize)(random < 0.5)

  def evaluate(s: Solution) = {
    localSearch(s)._2
  }

  def minimal = 0.0

  def ordering = scala.math.Ordering.Double

  def transform(solution: Solution) =
    mutateSolution(solution)

  def transform(solution1: Solution, solution2: Solution) =
    mutateSolutions(recombineSolutions(solution1, solution2))

  def mutateSolution(s: Solution) =
    if (random < mutationChance)
      s.map(f => if (random < mutationRate) !f else f)
    else
      s

  def mutateSolutions(s: (Solution, Solution)) =
    (mutateSolution(s._1), mutateSolution(s._2))

  def recombineSolutions(s1: Solution, s2: Solution): (Solution, Solution) = {
    val (s3, s4) = s1.zip(s2).map(recombineFeatures).unzip
    (s3.toArray, s4.toArray)
  }

  def recombineFeatures(features: (Feature, Feature)): (Feature, Feature) = {
    val (a, b) = features
    if (a != b && random < 0.5) {
      (b, a)
    } else {
      (a, b)
    }
  }
}


