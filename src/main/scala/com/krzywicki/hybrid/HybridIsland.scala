package com.krzywicki.hybrid

import com.krzywicki.util.MAS._
import akka.actor._
import com.krzywicki.stat.Statistics
import com.krzywicki.stat.Statistics._
import com.krzywicki.emas.EmasIsland

object HybridIsland {

  case object Loop

  def props(stats: Statistics) = Props(classOf[HybridIsland], stats)
}

class HybridIsland(implicit val stats: Statistics) extends EmasIsland {

  import HybridIsland._

  var population = createPopulation
  stats.update(population.maxBy(_.fitness).fitness, 0L)
  self ! Loop

  override def receive = super.receive orElse {
    case Loop =>
      population = population.groupBy(behaviour).flatMap(migration orElse monitored(meetings)).toList
      self ! Loop
  }

  def addAgent(agent: Agent) = population :+= agent
}