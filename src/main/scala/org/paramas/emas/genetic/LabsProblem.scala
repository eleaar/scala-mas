package org.paramas.emas.genetic

import java.util

import org.paramas.emas.random.RandomGenerator

/**
 * Created by Daniel on 2014-09-23.
 */
trait LabsProblem extends GeneticOps[LabsProblem] with RandomGenerator {
  type Feature = Boolean
  type Solution = Array[Feature]
  type Evaluation = Double

  def problemSize: Int

  def mutationChance: Double

  def mutationRate: Double

  def generate = Array.fill(problemSize)(random < 0.5)

  def evaluate(s: Solution) = {
    localSearch(s)._2
  }

  def energy(s: Solution) = {
    val L = s.size

    var energy = 0.0
    for (k <- 1 until L) {
      var ck = 0.0
      for (i <- 0 until L - k) {
        ck += (if (s(i) == s(i + k)) 1.0 else -1.0)
      }
      energy += ck * ck
    }

    L * L / (2.0 * energy)
  }

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

  def localSearch(s: Solution): (Solution, Evaluation) = {
    def localSearch0(stepsRemaining: Int, currentSolution: Solution, currentEvaluation: Evaluation): (Solution, Evaluation) =
      stepsRemaining match {
        case 0 => (currentSolution, currentEvaluation)
        case _ =>
          val flip = new OneBitFastFlipAlgorithm(currentSolution)
          val (bestFlippedBit, bestFlippedEvaluation) =
            (0 until s.size)
              .map(i => (i, flip.energy(i)))
              .maxBy(_._2)

          if (bestFlippedEvaluation > currentEvaluation) {
            // Side effect!
            currentSolution(bestFlippedBit) = !currentSolution(bestFlippedBit)
            localSearch0(stepsRemaining - 1, currentSolution, bestFlippedEvaluation)
          } else {
            (currentSolution, currentEvaluation)
          }
      }

    val maxIterations = 25
    // No side effects
    localSearch0(maxIterations, s.clone(), energy(s))
  }

  class OneBitFastFlipAlgorithm(s: Solution) {
    private val size = s.size
    private val computedProducts = Array.ofDim[Double](size - 1, size - 1)
    private val correlations = Array.ofDim[Double](size - 1)

    {
      var i = 0
      while (i < size - 1) {
        var j = 0
        while (j < size - 1 - i) {
          computedProducts(i)(j) = if (s(j) != s(j + i + 1)) 1.0 else -1.0

          j += 1
        }

        i += 1
      }
    }

    {
      var i = 0
      while (i < size - 1) {
        var sum = 0.0
        var j = 0
        while (j < size - 1 - i) {
          sum += computedProducts(i)(j)

          j += 1
        }
        correlations(i) = sum

        i += 1
      }
    }

    def energy(flipBitIndex: Int): Double = {
      val sizeDouble = size.toDouble

      var energy = 0.0
      var k = 0
      while (k < size - 1) {
        var correlation = correlations(k)
        if (k < size - flipBitIndex - 1) correlation -= 2.0 * computedProducts(k)(flipBitIndex)
        if (k < flipBitIndex) correlation -= 2.0 * computedProducts(k)(flipBitIndex - k - 1)
        energy += correlation * correlation

        k += 1
      }

      sizeDouble * sizeDouble / (2.0 * energy)
    }
  }

}


