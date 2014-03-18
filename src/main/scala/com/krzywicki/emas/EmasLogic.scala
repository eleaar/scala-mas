package com.krzywicki.emas

import com.krzywicki.util.MAS._

import com.krzywicki.config.AppConfig

trait EmasLogic {
  type Agent = com.krzywicki.util.MAS.Agent

  def initialPopulation: Population
  def behaviourFunction: BehaviourFun
  def meetingsFunction: Meetings
}



class EmasLogicImpl(implicit config: AppConfig) extends EmasLogic {
  def initialPopulation = createPopulation _
  def behaviourFunction = behaviour _
  def meetingsFunction = meetings _
}
