package org.scalamas.app

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.scalamas.random.ConcurrentRandomGenerator
import org.scalamas.stats.ConcurrentStatsFactory

/**
 * Created by Daniel on 2015-01-14.
 */
class ConcurrentStack(name: String)
  extends ConcurrentAgentRuntimeComponent
  with ConcurrentStatsFactory
  with ConcurrentRandomGenerator {

  val agentRuntime = new ConcurrentAgentRuntime {

    val config: Config = ConfigFactory.load()

    val system: ActorSystem = ActorSystem(name)
  }

}
