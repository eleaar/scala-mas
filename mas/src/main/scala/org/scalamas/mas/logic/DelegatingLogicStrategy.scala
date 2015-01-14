package org.scalamas.mas.logic

import org.scalamas.mas.{Logic, LogicStrategy}

/**
 * Created by Daniel on 2015-01-14.
 */
trait DelegatingLogicStrategy extends LogicStrategy {
  this: PopulationStrategy
    with BehaviourStrategy
    with MeetingsStrategy =>

  def logic = DelegatingLogic

  object DelegatingLogic extends Logic {
    def initialPopulation = populationStrategy.initialPopulation

    def behaviours = behaviourStrategy.behaviours

    def behaviourFunction = behaviourStrategy.behaviourFunction

    def meetingsFunction = meetingsStrategy.meetingsFunction
  }

}
