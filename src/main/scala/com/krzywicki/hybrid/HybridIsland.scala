package com.krzywicki.hybrid

import com.krzywicki.util.{Migrator, Statistics}
import com.krzywicki.util.MAS._
import akka.actor._
import com.krzywicki.concur.ConcurrentConfig

object HybridIsland {

  case object Loop

  def props(migrator: ActorRef, stats: Statistics) = Props(classOf[HybridIsland], migrator, stats)
}

class HybridIsland(val migrator: ActorRef, val stats: Statistics) extends Actor with ActorLogging {

  import HybridIsland._
  import Migrator._

  implicit val settings = ConcurrentConfig(context.system)

  var population = createPopulation
  stats.update(getBestFitness(population), 0L)
  migrator ! RegisterIsland(self)

  def receive = {
    case Loop =>
      val arenas = population.groupBy(behaviour)
      population = arenas.flatMap(migration orElse meetings).toList

      stats.update(getBestFitness(population), arenaCount(arenas, Reproduction))
      self ! Loop

    case Add(a) =>
      population :+= a
      stats.update(a.fitness, 0L)
  }

  def migration: PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Migration, agents) =>
      migrator ! MigrateAgents(agents);
      List.empty
  }

  def getBestFitness(population: Population) = population.maxBy(_.fitness).fitness

  def arenaCount[T <: Behaviour](groups: Map[T, List[Agent]], beh: T) = groups.getOrElse(beh, Seq.empty).size

}