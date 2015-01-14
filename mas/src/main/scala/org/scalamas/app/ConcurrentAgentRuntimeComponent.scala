package org.scalamas.app

import akka.actor.ActorSystem

/**
 * Created by Daniel on 2015-01-14.
 */
trait ConcurrentAgentRuntimeComponent extends AgentRuntimeComponent{

  def agentRuntime: ConcurrentAgentRuntime

  trait ConcurrentAgentRuntime extends AgentRuntime{
    def system: ActorSystem
  }
}
