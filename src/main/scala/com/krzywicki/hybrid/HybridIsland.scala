package com.krzywicki.hybrid

import com.krzywicki.util.Migrator
import com.krzywicki.util.MAS._
import akka.actor._
import com.krzywicki.concur.ConcurrentConfig
import com.krzywicki.stat.Statistics
import com.krzywicki.stat.Statistics._

object HybridIsland {

  case object Loop

  def props(migrator: ActorRef, stats: Statistics) = Props(classOf[HybridIsland], migrator, stats)
}

class HybridIsland(val migrator: ActorRef, implicit val stats: Statistics) extends Actor with ActorLogging {

  import HybridIsland._
  import Migrator._

  implicit val settings = ConcurrentConfig(context.system)

  var population = createPopulation
  stats.update(population.maxBy(_.fitness).fitness, 0L)
  migrator ! RegisterIsland(self)

  def receive = {
    case Loop =>
      population = population.groupBy(behaviour).flatMap(migration orElse monitored(meetings)).toList
      self ! Loop

    case Add(a) =>
      population :+= a
  }

  def migration: PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Migration, agents) =>
      migrator ! MigrateAgents(agents);
      List.empty
  }
}