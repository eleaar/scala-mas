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
package pl.edu.agh.scalamas.genetic

import pl.edu.agh.scalamas.app.AgentRuntimeComponent
import pl.edu.agh.scalamas.random.RandomGeneratorComponent

/**
 * An implementation of genetic operators for finding the maximu of the Labs function.
 */

trait LabsProblem extends GeneticProblem {
  this: AgentRuntimeComponent with RandomGeneratorComponent =>

  type Genetic = LabsOps

  def genetic = new LabsOps with SteepestDescend {
    def config = agentRuntime.config.getConfig("genetic.labs")

    val problemSize = config.getInt("problemSize")
    val mutationChance = config.getDouble("mutationChance")
    val mutationRate = config.getDouble("mutationRate")

    def random = LabsProblem.this.random.nextDouble()
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
    val result = localSearch(s)
    val evaluation = result._2
    evaluation
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

  def mutateSolutions(s: (Solution, Solution)) = s match {
    case (solution1, solution2) => (mutateSolution(solution1), mutateSolution(solution2))
  }

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