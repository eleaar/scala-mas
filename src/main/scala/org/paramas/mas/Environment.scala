package org.paramas.mas

import akka.actor.Actor
import org.paramas.emas.config.AppConfig
import org.paramas.mas.LogicTypes._

abstract class Environment extends Actor {
  import RootEnvironment._

  implicit val settings = AppConfig(context.system)

  def receive = {
    case Add(a) => addAgent(a)
  }

  def addAgent(agent: Agent)

  def migration: MeetingFunction = {
    case (Migration(cap), agents) =>
//      agents grouped(cap) foreach { context.parent ! Migrate(_)}
      context.parent ! Migrate(agents);
      List.empty
  }
}
