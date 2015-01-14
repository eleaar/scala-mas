package org.scalamas.app

import com.typesafe.config.{Config, ConfigFactory}
import org.scalamas.random.DefaultRandomGenerator
import org.scalamas.stats.SimpleStatsFactory

/**
 * Created by Daniel on 2015-01-14.
 */
class SequentialStack
  extends AgentRuntimeComponent
  with SimpleStatsFactory
  with DefaultRandomGenerator {

  val agentRuntime = new AgentRuntime {

    val config: Config = ConfigFactory.load()
  }
}
