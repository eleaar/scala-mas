package org.paramas.emas.stat

import org.paramas.emas.stat.Statistics._
import org.paramas.mas.Logic
import org.paramas.emas.EmasLogic
import org.paramas.emas.EmasLogic._

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


