package com.krzywicki.hybrid

import akka.actor._
import com.krzywicki.mas.{Logic, Environment}

object HybridIsland {

  case object Loop

  def props(logic: Logic) = Props(classOf[HybridIsland], logic)
}

class HybridIsland(logic: Logic) extends Environment {

  import HybridIsland._
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