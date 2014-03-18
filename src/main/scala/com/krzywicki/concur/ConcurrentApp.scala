package com.krzywicki.concur

import com.krzywicki.util.{Logger, Migrator, Reaper}
import com.krzywicki.util.Util._
import akka.actor.{PoisonPill, ActorSystem}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.event.Logging
import com.krzywicki.stat.Statistics


object ConcurrentApp {

  def main(args: Array[String]) {
    val islandsNumber = 1
    val duration = 10 seconds

    implicit val system = ActorSystem("concurrent")

    val stats = Statistics()

    val migrator = system.actorOf(Migrator.props, "migrator")
    val islands = List.tabulate(islandsNumber)(i => system.actorOf(ConcurrentIsland.props(migrator, stats), s"island$i"))
    system.scheduler.scheduleOnce(duration)(islands.foreach(_ ! PoisonPill))

    val log = Logging(system, getClass)
    Logger(frequency = 1 second) {
      time =>
        val (fitness, reproductions) = stats()
        log info (s"fitness $time $fitness")
        log info (s"reproductions $time $reproductions")
    }

    for (
      _ <- Reaper.actorsTerminate(islands);
      _ <- stats.updatesDone) {
      system.shutdown()
    }
  }
}