package org.paramas.mas.sync

import akka.actor._
import org.paramas.mas.{RootEnvironment, Logic}

object SyncEnvironment {

  case object Loop

  def props(logic: Logic) = Props(classOf[SyncEnvironment], logic)
}

class SyncEnvironment(logic: Logic) extends Actor {
  import RootEnvironment._
  import SyncEnvironment._
  import logic._

  var population = initialPopulation
  self ! Loop

  override def receive = {
    case Loop =>
      population = population.groupBy(behaviourFunction).flatMap(migration orElse meetingsFunction).toList
      self ! Loop
    case Add(agent) => population :+= agent
  }

  def updatePopulation = {
    population = population.groupBy(behaviourFunction).flatMap(migration orElse meetingsFunction).toList
  }

}