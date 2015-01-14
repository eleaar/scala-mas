package org.scalamas.app

import com.typesafe.config.Config

/**
 * Created by Daniel on 2015-01-14.
 */
trait AgentRuntimeComponent {

  def agentRuntime: AgentRuntime

  trait AgentRuntime {
    def config: Config
  }
}
