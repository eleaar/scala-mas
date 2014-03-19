package com.krzywicki.stat

import com.krzywicki.stat.Statistics._
import com.krzywicki.mas.Logic
import com.krzywicki.emas.EmasLogic
import com.krzywicki.emas.EmasLogic._

class MonitoredEmasLogic(val delegate: EmasLogic, implicit val stats: Statistics) extends Logic {
  def initialPopulation = {
    val population = delegate.initialPopulation
    stats.update(checked(population).maxBy(_.fitness).fitness, 0L)
    population
  }

  def behaviours = delegate.behaviours

  def behaviourFunction = delegate.behaviourFunction

  def meetingsFunction = monitored(delegate.meetingsFunction)
}

object MonitoredEmasLogic {
  def apply(delegate: EmasLogic)(implicit stats: Statistics) = new MonitoredEmasLogic(delegate, stats)
}


