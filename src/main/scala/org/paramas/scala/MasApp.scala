package org.paramas.scala

import com.krzywicki.util.{Migrator, Reaper}
import com.krzywicki.util.Util._
import akka.actor.{Cancellable, PoisonPill, ActorSystem}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.event.Logging
import com.krzywicki.stat.Statistics
import com.krzywicki.concur.ConcurrentIsland

object MasApp {

  def main(args: Array[String]) {
    val problemSize = if (args.length > 0) args(0).toInt else 100
    val islandsNumber = if (args.length > 1) args(1).toInt else 1
    val duration = if (args.length > 3) FiniteDuration(args(2).toLong, args(3)) else 10 seconds

    implicit val system = ActorSystem("concurrent")
    val log = Logging(system, getClass)

    val stats = Statistics()
    val migrator = system.actorOf(Migrator.props, "migrator")

    val islands = List.tabulate(islandsNumber)(i => system.actorOf(ConcurrentIsland.props(migrator, stats), s"island$i"))
    system.scheduler.scheduleOnce(duration)(islands.foreach(_ ! PoisonPill))

    val logger = Logger(frequency = 1 second) {
      time =>
        val (fitness, reproductions) = stats()
        log info (s"fitness $time $fitness")
        log info (s"reproductions $time $reproductions")
    }

    for (
      _ <- Reaper.actorsTerminate(islands);
      _ <- stats.updatesDone) {
      logger.cancel();
      system.shutdown()
    }
  }

  object Logger {

    class Ticker {
      // Will start ticking after the first call to 'time'
      lazy val startTime = System.currentTimeMillis()

      def time = System.currentTimeMillis() - startTime
    }

    def apply(frequency: Duration)(fun: (Long) => Unit)(implicit system: ActorSystem) = {
      val ticker = new Ticker
      val cancellable = system.scheduler.schedule(0 second, 1 second)(fun(ticker.time))
      new Cancellable {
        override def cancel(): Boolean = {
          fun(ticker.time)
          cancellable.cancel()
        }

        override def isCancelled: Boolean = cancellable.isCancelled
      }
    }
  }


}
