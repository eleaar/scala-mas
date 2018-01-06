/*
 * Copyright (c) 2013 Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pl.edu.agh.scalamas.genetic

import pl.edu.agh.scalamas.app.AgentRuntimeComponent
import pl.edu.agh.scalamas.random.RandomGeneratorComponent

// This is a hack to actually bypass the cake composition pattern, which turns out to be not so great... :(
trait ConfigurableProblem extends GeneticProblem with DefaultGeneticStats {
  self: AgentRuntimeComponent with RandomGeneratorComponent =>

  type Genetic = Ops

  def genetic = new Ops

  protected def hasStats = implicitly

  private trait Runtime extends AgentRuntimeComponent with RandomGeneratorComponent {
    def agentRuntime: AgentRuntime = new AgentRuntime {
      def config = self.agentRuntime.config
    }
    def randomData = self.randomData
  }

  private object rastrigin extends RastriginProblem with Runtime
  private object ackley extends AckleyProblem with Runtime
  private object griewank extends GriewankProblem with Runtime
  private object rosenbrock extends RosenbrockProblem with Runtime

  final class Ops extends GeneticOps[Ops] { self =>
    type Feature = Double
    type Solution = Array[Feature]
    type Evaluation = Double

    private val problemType = agentRuntime.config.getString("genetic.problem")
    private type SubOps = GeneticOps[Ops] {
      type Feature = self.Feature
      type Solution = self.Solution
      type Evaluation = self.Evaluation
    }
    private val subOps: SubOps = problemType match {
      case "rastrigin" => rastrigin.genetic.asInstanceOf[SubOps] // the casts are safe as we only know and use the same types
      case "ackley" => ackley.genetic.asInstanceOf[SubOps]
      case "griewank" => griewank.genetic.asInstanceOf[SubOps]
      case "rosenbrock" => rosenbrock.genetic.asInstanceOf[SubOps]
    }

    def transform(solution: Solution) = subOps.transform(solution)

    def transform(solution1: Solution, solution2: Solution) = subOps.transform(solution1, solution2)

    def generate = subOps.generate

    def evaluate(solution: Solution) = subOps.evaluate(solution)

    def minimal = subOps.minimal

    def ordering = subOps.ordering
  }
}
