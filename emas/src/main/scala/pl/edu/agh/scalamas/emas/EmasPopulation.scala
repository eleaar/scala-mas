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

package pl.edu.agh.scalamas.emas

import pl.edu.agh.scalamas.app.AgentRuntimeComponent
import pl.edu.agh.scalamas.emas.EmasTypes._
import pl.edu.agh.scalamas.emas.stats.EmasStats
import pl.edu.agh.scalamas.genetic.GeneticProblem
import pl.edu.agh.scalamas.mas.logic.PopulationStrategy
import pl.edu.agh.scalamas.random.RandomGeneratorComponent

/**
 * Default EMAS population strategy. It initializes a population of agents with some initial energy and initial solutions
 * generator from the genetic operators.
 *
 * It also updates the stats with the best fitness found in this initial population.
 *
 * Parameters:
 *  - emas.populationSize - The size of the initial population
 *  - emas.initialEnergy - The initial energy of initial agents
 */
trait EmasPopulation extends PopulationStrategy {
  this: AgentRuntimeComponent
    with GeneticProblem
    with EmasStats
    with RandomGeneratorComponent =>

  def populationStrategy = EmasPopulationProvider

  object EmasPopulationProvider extends PopulationProvider {
    private def config = agentRuntime.config.getConfig("emas")

    private def populationSize = config.getInt("populationSize")
    private def initialEnergy = config.getInt("initialEnergy")

    def initialPopulation = {
      implicit val ordering = genetic.ordering
      val population = List.fill(populationSize) {
        val solution = genetic.generate
        Agent[Genetic](solution, genetic.evaluate(solution), initialEnergy, generation = 0L, iteration = 0L)
      }
      stats.update((population.maxBy(_.fitness).fitness, 0L))
      population
    }
  }

}