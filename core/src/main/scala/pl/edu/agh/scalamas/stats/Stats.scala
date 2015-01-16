/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.scalamas.stats

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
  private[this] var oldValue = initialValue

  def update(newValue: T) = {
    oldValue = updateFunction(oldValue, newValue)
  }

  def get = Future.successful(oldValue)

  def getNow = oldValue
}

private[stats] class ConcurrentStats[T](initialValue: T, updateFunction: (T, T) => T, system: ActorSystem) extends Stats[T] {

  implicit val context = system.dispatcher

  // TODO: change system dep to exec context
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