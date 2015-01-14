package org.scalamas.stats

import org.scalamas.mas.AgentRuntimeComponent

/**
 * Created by Daniel on 2015-01-13.
 */
trait StatsFactoryComponent {

  def statsFactory: StatsFactory

  trait StatsFactory {
    def apply[T](initialValue: T)(updateFunction: (T, T) => T): Stats[T]
  }
}

trait SimpleStatsFactory extends StatsFactoryComponent {

  def statsFactory = SimpleStatsFactoryImpl

  object SimpleStatsFactoryImpl extends StatsFactory {
    def apply[T](initialValue: T)(updateFunction: (T, T) => T) = Stats.simple(initialValue)(updateFunction)
  }
}

trait ConcurrentStatsFactory extends StatsFactoryComponent {
  this: AgentRuntimeComponent =>

  def statsFactory = ConcurrentStatsFactoryImpl

  object ConcurrentStatsFactoryImpl extends StatsFactory {
    def apply[T](initialValue: T)(updateFunction: (T, T) => T) = Stats.concurrent(initialValue)(updateFunction)(agentRuntime.system)
  }
}
