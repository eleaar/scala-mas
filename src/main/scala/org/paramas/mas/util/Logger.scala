/**
 * Copyright (C) 2013 - 2014, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * This file is part of ParaphraseAGH/Scala.
 *
 * ParaphraseAGH/Scala is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ParaphraseAGH/Scala is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ParaphraseAGH/Scala.  If not, see <http://www.gnu.org/licenses/>.
*/

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
  def apply(frequency: FiniteDuration)(fun: (Long) => Unit)(implicit system: ActorSystem) = {
    val ticker = new LazyTicker
    system.scheduler.schedule(0 second, frequency)(fun(ticker.time))
  }
}
