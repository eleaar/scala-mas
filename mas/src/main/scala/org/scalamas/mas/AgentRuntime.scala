package org.scalamas.mas

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

trait AgentRuntimeComponent {

  def agentRuntime: AgentRuntime

  trait AgentRuntime {
    def system: ActorSystem

    def config: Config
  }
}

class DefaultAgentRuntime(name: String) extends AgentRuntimeComponent {
  val agentRuntime = new AgentRuntime {

    val config: Config = ConfigFactory.load()

    val system: ActorSystem = ActorSystem(name)
  }
}
