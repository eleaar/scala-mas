package com.krzywicki.hybrid

import scala.concurrent.duration._
import com.krzywicki.emas.{EmasLogicImpl, EmasApp}
import com.krzywicki.stat.{MonitoredEmasLogic, Statistics}
import com.krzywicki.config.AppConfig
import com.krzywicki.util.MAS._
import com.krzywicki.stat.Statistics._
import akka.actor.{Props, ActorSystem}
import akka.event.Logging
import com.krzywicki.util.{Reaper, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import com.krzywicki.mas.RootEnvironment

object HybridApp extends EmasApp {

  def main(args: Array[String]) {
    val name = "hybrid"
    val duration = 10 seconds

    implicit val system = ActorSystem(name)
    implicit val settings = AppConfig(system)
    implicit val stats = Statistics()
    val log = Logging(system, getClass)
    Logger(frequency = 1 second) {
      time =>
        val (fitness, reproductions) = stats()
        log info (s"fitness $time $fitness")
        log info (s"reproductions $time $reproductions")
    }

    val islandProps = HybridIsland.props(MonitoredEmasLogic(new EmasLogicImpl))

    val root = system.actorOf(RootEnvironment.props(islandProps), "root")
    for (
      _ <- Reaper.terminateAfter(root, duration);
      _ <- stats.updatesDone) {
      system.shutdown()
    }
  }


}