package com.krzywicki.hybrid

import com.krzywicki.util.Config
import com.krzywicki.util.Genetic._
import com.krzywicki.util.MAS._
import akka.actor._
import scala.concurrent.duration._
import com.krzywicki.util.Logger

object HybridIsland {

  case object Start

  case object Stop

  case object StopAck

  case object Loop

  case class Migrate(agent: Agent)

  case object PrintStats

  case class Stat[T](stat: String, source: ActorRef, data: T)

  def props(migrator: ActorRef)(implicit config: Config) = Props(classOf[HybridIsland], migrator, config)
}

class HybridIsland(val migrator: ActorRef, implicit val config: Config) extends Actor with ActorLogging {

  import HybridIsland._
  import context.dispatcher

  var population = createPopulation
  var bestFitness = getBestFitness(population)
  var reproductions = 0L

  def receive = {
    case Start =>
      scheduleNextStats
      self ! Loop

    case Loop =>
      val arenas = population.groupBy(behaviour)
      population = arenas.flatMap(migration orElse meetings).toList

      reproductions += arenaCount(arenas, Reproduction)
      bestFitness = math.max(bestFitness, getBestFitness(population))
      self ! Loop

    case Migrate(a) =>
      population :+= a
      bestFitness = math.max(bestFitness, a.fitness)

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
    log info s"fitness $self $bestFitness"
    log info s"reproductions $self $reproductions"
//    context.system.eventStream.publish(Stat("fitness", self, bestFitness))
//    context.system.eventStream.publish(Stat("reproductions", self, reproductionCount))
  }

  def getBestFitness(population: Population) = population.maxBy(_.fitness).fitness

  def scheduleNextStats = context.system.scheduler.scheduleOnce(1 second, self, PrintStats)

  def arenaCount[T <: Behaviour](groups: Map[T, List[Agent]], beh: T) = groups.getOrElse(beh, Seq.empty).size

}