package org.scalamas.mas.logic

import org.scalamas.mas.LogicTypes._

/**
 * Created by Daniel on 2015-01-14.
 */
trait PopulationStrategy {

  def populationStrategy: PopulationProvider

  trait PopulationProvider {

    def initialPopulation: Population

  }

}
