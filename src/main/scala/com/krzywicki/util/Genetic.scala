package com.krzywicki.util
import scala.collection.JavaConversions._
import scala.math._

object Genetic {

  type Feature = Double
  type Solution = Array[Feature]
  type Fitness = Double
  type Energy = Int
  type Agent = (Solution, Fitness, Energy)
  type Population = List[Agent]

  def createSolution(implicit config: Config): Solution =
    Array.fill(config.problemSize)(-50 + random * 100)

  def evaluateSolution(solution: Solution): Fitness =
    solution.foldLeft(0.0)(
      (sum, x) =>
        sum + 10 + x * x - 10 * cos(2 * Pi * x))

  def createAgent(implicit config: Config): Agent = {
    val solution = createSolution
    (solution, evaluateSolution(solution), config.initialEnergy)
  }

  def createPopulation(implicit config: Config): Population = {
    List.fill(config.populationSize)(createAgent)
  }

  def reproduce(s: Solution)(implicit config: Config) = mutateSolution(s)

  def reproduce(s1: Solution, s2: Solution)(implicit config: Config) =
    mutateSolutions(recombineSolutions(s1, s2))

  def mutateSolution(s: Solution)(implicit config: Config) =
    if (random < config.mutationChance)
      s.map(f => if (random < config.mutationRate) mutateFeature(f) else f)
    else
      s

  def mutateSolutions(s: (Solution, Solution))(implicit config: Config) =
    (mutateSolution(s._1), mutateSolution(s._2))

  def mutateFeature(f: Feature)(implicit config: Config): Feature = {
    val range = math.random match {
      case x if x < 0.2 => 5.0
      case x if x < 0.4 => 0.2
      case _ => 1.0
    }
    f + range * tan(Pi * (random - 0.5))
  }

  def recombineSolutions(s1: Solution, s2: Solution): (Solution, Solution) = {
    val (s3, s4) = s1.zip(s2).map(recombineFeatures).unzip
    (s3.toArray, s4.toArray)
  }

  def recombineFeatures(features: (Feature, Feature)): (Feature, Feature) = {
    val a = min(features._1, features._2)
    val b = max(features._1, features._2)
    (a + (b - a) * random, a + (b - a) * random)
  }
}