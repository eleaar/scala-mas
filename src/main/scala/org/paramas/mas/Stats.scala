package org.paramas.mas

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.agent.Agent

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

private[mas] class SimpleStats[T](initialValue: T, updateFunction: (T, T) => T) extends Stats[T] {
  private var oldValue = initialValue

  def update(newValue: T) = {
    oldValue = updateFunction(oldValue, newValue)
  }

  def get = Future.successful(oldValue)

  def getNow = oldValue
}

private[mas] class ConcurrentStats[T](initialValue: T, updateFunction: (T, T) => T, system: ActorSystem) extends Stats[T] {

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
