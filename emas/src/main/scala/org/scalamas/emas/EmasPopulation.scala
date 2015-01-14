package org.scalamas.emas

import org.scalamas.genetic.GeneticProblem
import org.scalamas.mas.AgentRuntimeComponent
import org.scalamas.mas.logic.PopulationStrategy
import org.scalamas.mas.random.RandomGenerator

/**
 * Created by Daniel on 2015-01-14.
 */
trait EmasPopulation extends PopulationStrategy {
  this: AgentRuntimeComponent
    with GeneticProblem
    with EmasStats
    with RandomGenerator =>

  def populationStrategy = EmasPopulationProvider

  object EmasPopulationProvider extends PopulationProvider {

    import org.scalamas.emas.EmasTypes._

    def config = agentRuntime.config.getConfig("emas")

    val populationSize = config.getInt("populationSize")
    val initialEnergy = config.getInt("initialEnergy")

    implicit val ordering = genetic.ordering

    def initialPopulation = {
      val population = List.fill(populationSize) {
        val solution = genetic.generate
        Agent[Genetic](solution, genetic.evaluate(solution), initialEnergy)
      }
      population.maxBy(_.fitness).fitness
      stats.update((population.maxBy(_.fitness).fitness, 0L))
      population
    }
  }

}
