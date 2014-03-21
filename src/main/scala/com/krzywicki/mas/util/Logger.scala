package com.krzywicki.mas.util

import scala.concurrent.duration._
import akka.actor.{Cancellable, ActorSystem}
import scala.concurrent.ExecutionContext.Implicits.global

class Ticker {
  // Will start ticking after the first call to 'time'
  lazy val startTime = System.currentTimeMillis()

  def time = System.currentTimeMillis() - startTime
}

object Logger {
  def apply(frequency: Duration)(fun: (Long) => Unit)(implicit system: ActorSystem) = {
    val ticker = new Ticker
    system.scheduler.schedule(0 second, 1 second)(fun(ticker.time))
  }
}
