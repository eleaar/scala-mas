package com.krzywicki.hybrid

import akka.actor._
import com.krzywicki.emas.{EmasLogic, EmasIsland}

object HybridIsland {

  case object Loop

  def props(logic: EmasLogic) = Props(classOf[HybridIsland], logic)
}

class HybridIsland(logic: EmasLogic) extends EmasIsland {

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