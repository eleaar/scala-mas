package com.krzywicki.mas

import com.krzywicki.util.MAS._

import com.krzywicki.config.AppConfig

trait Logic {
  type Agent = com.krzywicki.util.MAS.Agent

  def initialPopulation: Population
  def behaviourFunction: BehaviourFun
  def meetingsFunction: Meetings
}




