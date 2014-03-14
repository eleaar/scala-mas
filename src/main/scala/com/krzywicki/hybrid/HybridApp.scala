package com.krzywicki.hybrid

import com.krzywicki.util.{Migrator, Reaper}
import com.krzywicki.util.Util._
import akka.actor.{PoisonPill, ActorSystem}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import akka.pattern._
import akka.event.Logging
import com.krzywicki.stat.Statistics


object HybridApp {

  def main(args: Array[String]) {
    val problemSize = if (args.length > 0) args(0).toInt else 100
    val islandsNumber = if (args.length > 1) args(1).toInt else 1
    val duration = if (args.length > 3) FiniteDuration(args(2).toLong, args(3)) else 10 seconds

    implicit val system = ActorSystem("hybrid")

    val stats = Statistics()
    val migrator = system.actorOf(Migrator.props, "migrator")
    val islands = List.tabulate(islandsNumber)(i => system.actorOf(HybridIsland.props(migrator, stats), s"island$i"))

    islands.foreach(_ ! HybridIsland.Loop)
    system.scheduler.scheduleOnce(duration)(islands.foreach(_ ! PoisonPill))

    val log = Logging(system, getClass)
    val startTime = System.currentTimeMillis()
    def time = System.currentTimeMillis() - startTime
    def printLogs(x: (Double, Long)) = using(time) {
      t =>
        log info (s"fitness $t ${x._1}")
        log info (s"reproductions $t ${x._2}")
    }

    val loggerTicks = system.scheduler.schedule(0 second, 1 second)(printLogs(stats()))
    for (
      _ <- Reaper.actorsTerminate(islands);
      _ <- stats.updatesDone) {
      loggerTicks.cancel();
      printLogs(stats())
      system.shutdown()
    }
  }


}