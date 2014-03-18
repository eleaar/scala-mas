package com.krzywicki.hybrid

import com.krzywicki.util.MAS._
import akka.actor._
import com.krzywicki.stat.Statistics
import com.krzywicki.stat.Statistics._
import com.krzywicki.emas.EmasRoot
import com.krzywicki.config.{ConcurrentConfig, AppConfig}

object HybridIsland {

  case object Loop

  def props(stats: Statistics) = Props(classOf[HybridIsland], stats)
}

class HybridIsland(implicit val stats: Statistics) extends Actor with ActorLogging {

  import HybridIsland._
  import EmasRoot._

  implicit val settings = AppConfig(context.system)

  var population = createPopulation
  stats.update(population.maxBy(_.fitness).fitness, 0L)
  self ! Loop

  def receive = {
    case Loop =>
      population = population.groupBy(behaviour).flatMap(migration orElse monitored(meetings)).toList
      self ! Loop

    case Add(a) =>
      population :+= a
  }

  def migration: PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Migration, agents) =>
      context.parent ! Migrate(agents);
      List.empty
  }
}