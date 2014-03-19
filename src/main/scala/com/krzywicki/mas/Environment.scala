package com.krzywicki.mas

import akka.actor.Actor
import com.krzywicki.config.AppConfig
import com.krzywicki.mas.LogicTypes._

abstract class Environment extends Actor {
  import RootEnvironment._

  implicit val settings = AppConfig(context.system)

  def receive = {
    case Add(a) => addAgent(a)
  }

  def addAgent(agent: Agent)

  def migration: MeetingFunction = {
    case (Migration, agents) =>
      context.parent ! Migrate(agents);
      List.empty
  }
}
