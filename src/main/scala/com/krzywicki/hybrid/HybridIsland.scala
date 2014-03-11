package com.krzywicki.hybrid

import com.krzywicki.util.Config
import com.krzywicki.util.Genetic._
import com.krzywicki.util.MAS._
import akka.actor._
import scala.concurrent.duration._
import com.krzywicki.util.Util._
import HybridIsland._

object HybridIsland {

  case object Start

  case object Stop

  case object StopAck

  case object Loop

  case class Migrate(agent: Agent)

  case object PrintStats

  type Stats = akka.agent.Agent[(Double, Long)]

  def props(migrator: ActorRef, stats: Stats)(implicit config: Config) = Props(classOf[HybridIsland], migrator, stats, config).withDispatcher("agent-dispatcher")
}

class HybridIsland(val migrator: ActorRef, val stats: Stats, implicit val config: Config) extends Actor with ActorLogging {

  import HybridIsland._
  import context.dispatcher

  var population = createPopulation
  var bestFitness = getBestFitness(population)
  var reproductions = 0L

  def receive = {
    case Start =>
      publishStats
      scheduleNextStats
      self ! Loop

    case Loop =>
      val arenas = population.groupBy(behaviour)
      population = arenas.flatMap(migration orElse meetings).toList

      reproductions += arenaCount(arenas, Reproduction)
      bestFitness = math.max(bestFitness, getBestFitness(population))
      self ! Loop
//      publishStats

    case Migrate(a) =>
      population :+= a
      bestFitness = math.max(bestFitness, a.fitness)
//      publishStats

    case PrintStats =>
      publishStats
      scheduleNextStats

    case Stop =>
      publishStats
      context stop self
  }

  def migration: PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Migration, agents) =>
      migrator ! HybridMigrator.RecieveEmigrants(agents);
      List.empty
  }

  def publishStats = {
    using((bestFitness, reproductions)) {
      case (newF, newR) =>
          stats send ((oldF: Double, oldR: Long) => (math.max(oldF, newF), oldR + newR)).tupled
    }
    reproductions = 0
  }

  def getBestFitness(population: Population) = population.maxBy(_.fitness).fitness

  def scheduleNextStats = context.system.scheduler.scheduleOnce(1 second, self, PrintStats)

  def arenaCount[T <: Behaviour](groups: Map[T, List[Agent]], beh: T) = groups.getOrElse(beh, Seq.empty).size

}