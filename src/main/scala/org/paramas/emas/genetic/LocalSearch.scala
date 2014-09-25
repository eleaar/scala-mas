package org.paramas.emas.genetic

/**
 * Created by Daniel on 2014-09-25.
 */
trait LocalSearch[G <: GeneticOps[G]] {
  def localSearch(s: G#Solution): (G#Solution, G#Evaluation)
}

trait SteepestDescend extends LocalSearch[LabsProblem] {

  def maxIterations = 25

  def localSearch(s: LabsProblem#Solution) = {
    def localSearch0(stepsRemaining: Int, currentSolution: LabsProblem#Solution, flipper: OneBitFastFlipper):
    (LabsProblem#Solution, LabsProblem#Evaluation) =
      stepsRemaining match {
        case 0 => (currentSolution, flipper.currentEnergy)
        case _ =>
          val (bestFlippedBit, bestFlippedEvaluation) =
            (0 until s.size)
              .map(i => (i, flipper.energyWithFlipped(i)))
              .maxBy(_._2)

          if (bestFlippedEvaluation > flipper.currentEnergy) {
            // Side effect!
            currentSolution(bestFlippedBit) = !currentSolution(bestFlippedBit)
            localSearch0(stepsRemaining - 1, currentSolution, OneBitFastFlipper(currentSolution))
          } else {
            (currentSolution, flipper.currentEnergy)
          }
      }
    // No side effects for s
    localSearch0(maxIterations, s.clone(), OneBitFastFlipper(s))
  }

  case class OneBitFastFlipper(s: Array[Boolean]) {
    private val size = s.size
    private val computedProducts = Array.ofDim[Double](size - 1, size - 1)
    private val correlations = Array.ofDim[Double](size - 1)

    for (i <- 0 until size - 1;
         j <- 0 until size - 1 - i) {
      computedProducts(i)(j) = if (s(j) != s(j + i + 1)) 1.0 else -1.0
      correlations(i) += computedProducts(i)(j)
    }

    def currentEnergy = size * size / (2.0 * correlations.map(x => x * x).sum)

    def energyWithFlipped(flipBitIndex: Int): Double = {
      var energy = 0.0

      for (k <- 0 until size - 1) {
        var correlation = correlations(k)
        if (k < size - flipBitIndex - 1) correlation -= 2.0 * computedProducts(k)(flipBitIndex)
        if (k < flipBitIndex) correlation -= 2.0 * computedProducts(k)(flipBitIndex - k - 1)
        energy += correlation * correlation
      }

      size * size / (2.0 * energy)
    }
  }