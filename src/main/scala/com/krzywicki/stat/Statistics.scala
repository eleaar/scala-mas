package com.krzywicki.stat

import akka.actor.ActorSystem
import akka.agent.Agent
import com.krzywicki.util.MAS._
import com.krzywicki.stat.MeetingsInterceptor._

object Statistics {
  def apply()(implicit system: ActorSystem) = new Statistics(system)

  def monitored(m: => Meetings)(implicit stats: Statistics) = m.intercepted {
    case (Reproduction, agentsBefore, agentsAfter) =>
      stats.update(agentsAfter.maxBy(_.fitness).fitness, agentsBefore.size)
  }
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
