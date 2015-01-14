package org.scalamas.mas.logic

import org.scalamas.mas.LogicTypes._

/**
 * Created by Daniel on 2015-01-14.
 */
trait BehaviourStrategy {

  def behaviourStrategy: BehaviourProvider

  trait BehaviourProvider {

    def behaviours: Seq[Behaviour]

    def behaviourFunction: BehaviourFunction
  }

}
