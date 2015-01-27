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
 * An implementation of genetic operators for finding the minimum of the Rastrigin function.
 */
trait RastriginProblem extends GeneticProblem {
  this: AgentRuntimeComponent with RandomGeneratorComponent =>

  type Genetic = RastriginOps

  def genetic = new RastriginOps

  class RastriginOps extends GeneticOps[RastriginOps] {

    type Feature = Double
    type Solution = Array[Feature]
    type Evaluation = Double

    def config = agentRuntime.config.getConfig("genetic.rastrigin")

    val problemSize = config.getInt("problemSize")
    val mutationChance = config.getDouble("mutationChance")
    val mutationRate = config.getDouble("mutationRate")

    def generate = Array.fill(problemSize)(randomData.nextUniform(-50.0, 50.0))

    def evaluate(solution: Solution) = {
      solution.foldLeft(0.0)(
        (sum, x) => sum + 10 + x * x - 10 * math.cos(2 * x * math.Pi))
    }

    // TODO take problemSize into account
    val minimal = 10000.0

    val ordering = Ordering[Double].reverse

    def transform(solution: Solution) =
      mutateSolution(solution)

    def transform(solution1: Solution, solution2: Solution) =
      mutateSolutions(recombineSolutions(solution1, solution2))

    def mutateSolution(s: Solution) =
      if (random.nextDouble < mutationChance)
        s.map(f => if (random.nextDouble < mutationRate) mutateFeature(f) else f)
      else
        s

    def mutateSolutions(s: (Solution, Solution)) = s match {
      case (solution1, solution2) => (mutateSolution(solution1), mutateSolution(solution2))
    }

    def mutateFeature(f: Feature): Feature = {
      val range = random.nextDouble match {
        case x if x < 0.2 => 5.0
        case x if x < 0.4 => 0.2
        case _ => 1.0
      }
      f + range * randomData.nextCauchy(0.0, 1.0)
    }

    def recombineSolutions(s1: Solution, s2: Solution): (Solution, Solution) = {
      val (s3, s4) = s1.zip(s2).map(recombineFeatures).unzip
      (s3.toArray, s4.toArray)
    }

    def recombineFeatures(features: (Feature, Feature)): (Feature, Feature) = features match {
      case f @ (feature1, feature2) if feature1 == feature2 => f
      case (feature1, feature2) =>
        val a = math.min(feature1, feature2)
        val b = math.max(feature1, feature2)
        (randomData.nextUniform(a, b), randomData.nextUniform(a, b))
    }
  }

}