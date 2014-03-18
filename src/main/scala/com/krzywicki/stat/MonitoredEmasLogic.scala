package com.krzywicki.stat

import com.krzywicki.stat.Statistics._
import com.krzywicki.mas.Logic

class MonitoredEmasLogic(val delegate: Logic, implicit val stats: Statistics) extends Logic {
  def initialPopulation = {
    val population = delegate.initialPopulation
    stats.update(population.maxBy(_.fitness).fitness, 0L)
    population
  }

  def behaviourFunction = delegate.behaviourFunction

  def meetingsFunction = monitored(delegate.meetingsFunction)
}

object MonitoredEmasLogic {
  def apply(delegate: Logic)(implicit stats: Statistics) = new MonitoredEmasLogic(delegate, stats)
}


