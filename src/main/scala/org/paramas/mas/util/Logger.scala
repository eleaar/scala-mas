package org.paramas.mas.util

import scala.concurrent.duration._
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Will start ticking after the first call to 'time'.
 */
private class LazyTicker {
  lazy val startTime = System.currentTimeMillis()

  def time = System.currentTimeMillis() - startTime
}

object Logger {

  /**
   * Executes the provided callback at some provided frequency, passing the relative time in ms.
   * The first call will receive a time of '0'ms, the following will receive time relative to the first call.
   */
  def apply(frequency: Duration)(fun: (Long) => Unit)(implicit system: ActorSystem) = {
    val ticker = new LazyTicker
    system.scheduler.schedule(0 second, 1 second)(fun(ticker.time))
  }
}
