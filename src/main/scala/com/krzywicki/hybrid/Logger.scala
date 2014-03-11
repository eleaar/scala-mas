package com.krzywicki.hybrid

import akka.actor._
import scala.concurrent.duration._

object Logger {

  case class Stat[T](stat: String, source: ActorRef, data: T)

  case object Flush

  def props(initialSouls: Seq[ActorRef]) = Props(classOf[Logger], initialSouls)
}

class Logger(islands: Seq[ActorRef]) extends Actor with ActorLogging {

  import Logger._

  islands foreach (context watch _)
  var islandsLeft = islands.toSet

  import context.dispatcher

  context.system.scheduler.schedule(1 second, 1 second, self, Flush)

  val startTime = System.currentTimeMillis()
  def time = System.currentTimeMillis() - startTime
  var counter = 0

  var bestFitness = Double.MinValue
  var reproductions = 0L

  def receive = {
    case Stat("fitness", _, data: Double) => bestFitness = math.max(bestFitness, data)
    case Stat("reproductions", _, data: Long) =>  reproductions += data
    case Terminated(island) =>
      islandsLeft -= island
      if (islandsLeft.isEmpty) {
        context.system.shutdown
        log info s"$time flush $counter"
      }
    case Flush =>
      log info s"$time fitness $bestFitness"
      log info s"$time reproductions $reproductions"
  }

  def update(name: String, data: Double) = name match {
    case "fitness" =>
    case "reproductions" =>
  }
}
