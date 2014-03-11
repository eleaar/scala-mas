package com.krzywicki.hybrid

import com.krzywicki.util.{Statistics, Config}
import com.krzywicki.util.MAS._
import akka.actor._

object HybridIsland {

  case object Loop

  case class Migrate(agent: Agent)

  def props(migrator: ActorRef, stats: Statistics)(implicit config: Config) = Props(classOf[HybridIsland], migrator, stats, config).withDispatcher("agent-dispatcher")
}

class HybridIsland(val migrator: ActorRef, val stats: Statistics, implicit val config: Config) extends Actor with ActorLogging {

  import HybridIsland._

  var population = createPopulation
  stats.update(getBestFitness(population), 0L)
  migrator ! HybridMigrator.RegisterIsland(self)

  def receive = {
    case Loop =>
      val arenas = population.groupBy(behaviour)
      population = arenas.flatMap(migration orElse meetings).toList

      stats.update(getBestFitness(population), arenaCount(arenas, Reproduction))
      self ! Loop

    case Migrate(a) =>
      population :+= a
      stats.update(a.fitness, 0L)
  }

  def migration: PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Migration, agents) =>
      migrator ! HybridMigrator.ReceiveEmigrants(agents);
      List.empty
  }

  def getBestFitness(population: Population) = population.maxBy(_.fitness).fitness

  def arenaCount[T <: Behaviour](groups: Map[T, List[Agent]], beh: T) = groups.getOrElse(beh, Seq.empty).size

}