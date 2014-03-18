package com.krzywicki.stat

import com.krzywicki.emas.EmasLogic
import com.krzywicki.stat.Statistics._

class MonitoredEmasLogic(val delegate: EmasLogic, implicit val stats: Statistics) extends EmasLogic {
  def initialPopulation = {
    val population = delegate.initialPopulation
    stats.update(population.maxBy(_.fitness).fitness, 0L)
    population
  }

  def behaviourFunction = delegate.behaviourFunction

  def meetingsFunction = monitored(delegate.meetingsFunction)
}

object MonitoredEmasLogic {
  def apply(delegate: EmasLogic)(implicit stats: Statistics) = new MonitoredEmasLogic(delegate, stats)
}


