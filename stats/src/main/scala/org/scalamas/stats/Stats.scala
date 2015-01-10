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

package org.paramas.stats

import akka.actor.ActorSystem
import akka.agent.Agent

import scala.concurrent.Future

/**
 * Interface for the gathering of statistics during computations. The statistics value is the result of folding in time all the provided updates.
 * @tparam T - compound type of the gathered statistics.
 */
trait Stats[T] {
  /**
   * Updates the value of the statistic in some point in the future.
   */
  def update(newValue: T)

  /**
   * Asynchronously returns the value of the statistics once all currently pending updates are done.
   */
  def get: Future[T]

  /**
   * Synchronously returns the current value of the statistics (may not reflect pending udpates).
   */
  def getNow: T
}

private[stats] class SimpleStats[T](initialValue: T, updateFunction: (T, T) => T) extends Stats[T] {
  private var oldValue = initialValue

  def update(newValue: T) = {
    oldValue = updateFunction(oldValue, newValue)
  }

  def get = Future.successful(oldValue)

  def getNow = oldValue
}

private[stats] class ConcurrentStats[T](initialValue: T, updateFunction: (T, T) => T, system: ActorSystem) extends Stats[T] {

  import system.dispatcher

  val stats = Agent(initialValue)

  def update(newValue: T) = stats send ((oldValue: T) => updateFunction(oldValue, newValue))

  def get = stats.future()

  def getNow = stats()
}

object Stats {

  /**
   * Factory method for synchronous, non thread-safe statistics.
   */
  def simple[T](initialValue: T)(updateFunction: (T, T) => T) = new SimpleStats[T](initialValue, updateFunction)

  /**
   * Factory method for asynchronous, thread-safe statistics.
   */
  def concurrent[T](initialValue: T)(updateFunction: (T, T) => T)(implicit system: ActorSystem) = new ConcurrentStats[T](initialValue, updateFunction, system)

}
