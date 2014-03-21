package com.krzywicki.mas.sync

import akka.actor._
import com.krzywicki.mas.{Environment, Logic}

import com.krzywicki.mas.LogicTypes._

object SyncEnvironment {
  case object Loop

  def props(logic: Logic) = Props(classOf[SyncEnvironment], logic)
}

class SyncEnvironment(logic: Logic) extends Environment {

  import SyncEnvironment._
  import logic._

  var population = initialPopulation
  self ! Loop

  override def receive = super.receive orElse {
    case Loop =>
      population = population.groupBy(behaviourFunction).flatMap(migration orElse meetingsFunction).toList
      self ! Loop
  }

  def addAgent(agent: Agent) = population :+= agent
}