package com.krzywicki.emas

import com.krzywicki.mas.util.{Logger, Reaper}
import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._
import akka.event.Logging
import com.krzywicki.emas.stat.{MonitoredEmasLogic, Statistics}
import scala.concurrent.ExecutionContext.Implicits.global
import com.krzywicki.mas.{Logic, RootEnvironment}
import com.krzywicki.emas.config.AppConfig
import com.krzywicki.mas.async.AsyncEnvironment
import com.krzywicki.mas.sync.SyncEnvironment

object Async extends EmasApp {

  def main(args: Array[String]) {
    run("concurrent", AsyncEnvironment.props, 10 seconds)
  }
}

object HybridApp extends EmasApp {

  def main(args: Array[String]) {
    run("hybrid", SyncEnvironment.props, 10 seconds)
  }
}

class EmasApp {

  def run(name: String, islandsProps: (Logic) => Props, duration: FiniteDuration) {
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

    val root = system.actorOf(RootEnvironment.props(islandsProps(MonitoredEmasLogic(new EmasLogic))), "root")
    for (
      _ <- Reaper.terminateAfter(root, duration);
      _ <- stats.updatesDone) {
      system.shutdown()
    }
  }
}
