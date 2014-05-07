package org.paramas.mas.sync

import akka.actor._
import org.paramas.mas.{RootEnvironment, Logic}

object SyncEnvironment {

  case object Loop

  def props(logic: Logic) = Props(classOf[SyncEnvironment], logic)
}

/**
 * A synchronous island implementation. This actor spins in a messages loop. In each iteration it updates synchronously
 * the population accordingly to the behaviour and meetings functions. This islands supports migration, assuming its
 * parent is a RootEnvironment.
 *
 * @param logic the callbacks for the simulation
 */
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
}