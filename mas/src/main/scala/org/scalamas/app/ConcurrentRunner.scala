package org.scalamas.app

import akka.event.Logging
import org.scalamas.mas.{EnvironmentStrategy, RootEnvironment}
import org.scalamas.stats.StatsComponent
import org.scalamas.util.{Logger, Reaper}

import scala.concurrent.duration._

/**
 * Created by Daniel on 2015-01-14.
 */
trait ConcurrentRunner {
  this: ConcurrentAgentRuntimeComponent
    with EnvironmentStrategy
    with StatsComponent =>

  val islands = agentRuntime.config.getInt("mas.islandsNumber")

  def run(duration: FiniteDuration): Unit = {

    implicit val system = agentRuntime.system
    import system.dispatcher

    val log = Logging(system, getClass)
    Logger(frequency = 1 second) {
      time =>
        log info (s"$time ${formatter(stats.getNow)}")
    }

    val root = system.actorOf(RootEnvironment.props(environmentProps, islands), "root")
    for (
      _ <- Reaper.terminateAfter(root, duration);
      _ <- stats.get) {
      system.shutdown()
    }
  }

}
