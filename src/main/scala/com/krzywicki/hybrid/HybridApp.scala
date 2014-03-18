package com.krzywicki.hybrid

import com.krzywicki.util.{Reaper, Logger}
import akka.actor.ActorSystem
import scala.concurrent.duration._
import akka.event.Logging
import com.krzywicki.stat.Statistics
import com.krzywicki.concur.ConcurrentIsland
import scala.concurrent.ExecutionContext.Implicits.global
import com.krzywicki.emas.EmasRoot

object HybridApp {

  def main(args: Array[String]) {
    val duration = 10 seconds

    implicit val system = ActorSystem("hybrid")

    val stats = Statistics()

    val log = Logging(system, getClass)
    Logger(frequency = 1 second) {
      time =>
        val (fitness, reproductions) = stats()
        log info (s"fitness $time $fitness")
        log info (s"reproductions $time $reproductions")
    }

    val root = system.actorOf(EmasRoot.props(ConcurrentIsland.props(stats)), "root")
    for (
      _ <- Reaper.terminateAfter(root, duration);
      _ <- stats.updatesDone) {
      system.shutdown()
    }
  }


}