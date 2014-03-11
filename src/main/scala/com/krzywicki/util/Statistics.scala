package com.krzywicki.util

import akka.actor.ActorSystem
import akka.agent.Agent

object Statistics {
  def apply()(implicit system: ActorSystem) = new Statistics(system)
}

class Statistics(system: ActorSystem) {

  import system.dispatcher
  val stats = Agent((Double.MinValue, 0L))

  def update(newF: Double, newR: Long) {
    stats send ((oldF: Double, oldR: Long) => (math.max(oldF, newF), oldR + newR)).tupled
  }

  def updatesDone = stats.future()

  def apply() = stats()
}
