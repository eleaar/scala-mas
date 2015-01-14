package org.scalamas.mas

import akka.actor.Props
import org.scalamas.mas.async.AsyncEnvironment
import org.scalamas.mas.sync.SyncEnvironment

/**
 * Created by Daniel on 2015-01-14.
 */
trait EnvironmentStrategy {

  def environmentProps: Props
}

trait SynchronousEnvironment extends EnvironmentStrategy {
  this: LogicStrategy =>

  def environmentProps = SyncEnvironment.props(logic)
}

trait AsynchronousEnvironment extends EnvironmentStrategy {
  this: LogicStrategy =>

  def environmentProps = AsyncEnvironment.props(logic)
}
