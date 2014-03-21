package com.krzywicki.emas.stat

import akka.actor.ActorSystem
import akka.agent.Agent
import com.krzywicki.emas.EmasLogic
import com.krzywicki.mas.LogicTypes._
import EmasLogic._
import com.krzywicki.mas.util.MeetingsInterceptor
import MeetingsInterceptor._

object Statistics {
  def apply()(implicit system: ActorSystem) = new Statistics(system)

  def monitored(m: => MeetingFunction)(implicit stats: Statistics) = m.intercepted {
    case (Reproduction(_), agentsBefore, agentsAfter) =>
      stats.update(checked(agentsAfter).maxBy(_.fitness).fitness, agentsBefore.size)
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
