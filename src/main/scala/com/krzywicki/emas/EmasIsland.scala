package com.krzywicki.emas

import akka.actor.Actor
import com.krzywicki.util.MAS._
import com.krzywicki.util.MAS.Agent
import com.krzywicki.config.AppConfig

abstract class EmasIsland extends Actor {

  import EmasRoot._

  implicit val settings = AppConfig(context.system)

  def receive = {
    case Add(a) => addAgent(a)
  }

  def addAgent(agent: Agent)

  def migration: Meetings = {
    case (Migration, agents) =>
      context.parent ! Migrate(agents);
      List.empty
  }
}
