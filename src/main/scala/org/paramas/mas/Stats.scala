package org.paramas.mas

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.agent.Agent

trait Stats[T] {
  def update(newValue: T)

  def get: Future[T]

  def getNow: T
}

class SimpleStats[T](initialValue: T, updateFunction: (T, T) => T) extends Stats[T] {
  private var oldValue = initialValue

  def update(newValue: T) = {
    oldValue = updateFunction(oldValue, newValue)
  }

  def get = Future.successful(oldValue)

  def getNow = oldValue
}

class ConcurrentStats[T](initialValue: T, updateFunction: (T, T) => T, system: ActorSystem) extends Stats[T] {

  import system.dispatcher

  val stats = Agent(initialValue)

  def update(newValue: T) = stats send ((oldValue: T) => updateFunction(oldValue, newValue))

  def get = stats.future()

  def getNow = stats()
}

object Stats {

  def simple[T](initialValue: T)(updateFunction: (T, T) => T) = new SimpleStats[T](initialValue, updateFunction)
  def concurrent[T](initialValue: T)(updateFunction: (T, T) => T)(implicit system: ActorSystem) = new ConcurrentStats[T](initialValue, updateFunction, system)

}
