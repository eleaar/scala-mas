/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.scalamas.genetic


trait LocalSearch[G <: GeneticOps[G]] {
  def localSearch(s: G#Solution): (G#Solution, G#Evaluation)
}

trait SteepestDescend extends LocalSearch[LabsOps] {

  def maxIterations = 25

  def localSearch(s: LabsOps#Solution) = {
    def localSearch0(stepsRemaining: Int, currentSolution: LabsOps#Solution, flipper: OneBitFastFlipper):
    (LabsOps#Solution, LabsOps#Evaluation) =
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