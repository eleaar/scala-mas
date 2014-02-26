package com.krzywicki.hybrid

import akka.actor.Actor
import com.krzywicki.util.Config
import com.krzywicki.util.Genetic._
import com.krzywicki.util.MAS._
import com.krzywicki.util.MAS.Agent
import akka.actor.ActorLogging
import akka.actor.Props
import scala.concurrent.duration._
import akka.actor.ActorRef

object HybridIsland {
  case class Start(migrator: ActorRef)
  case object Loop
  case class Migrate(agent: Agent)
  case object PrintStats

  def props(implicit config: Config) = Props(classOf[HybridIsland], config)
}

class HybridIsland(implicit config: Config) extends Actor with ActorLogging {
  import HybridIsland._
  import context.dispatcher

  var population = createPopulation
  var migrator: ActorRef = _

  def getBestFitness(population: Population) = population.maxBy(_.fitness).fitness
  var bestFitness: Fitness = Double.MinValue
  var reproductionCount = 0
  var migrationCount = 0

  def receive = {
    case Start(ref) =>
      migrator = ref
      bestFitness = getBestFitness(population)

      migrator ! HybridMigrator.RegisterIsland(self)
      scheduleNextStats
      self ! Loop

    case Loop =>
      val arenas = population.groupBy(behaviour)
      population = arenas.flatMap(migration orElse meetings).toList

      reproductionCount += arenaCount(arenas, Reproduction)
      migrationCount += arenaCount(arenas, Migration)
      bestFitness = math.max(bestFitness, getBestFitness(population))
      self ! Loop

    case Migrate(a) => population :+= a

    case PrintStats =>
      log.info(s"fitness $bestFitness")
      log.info(s"reproductionCount $reproductionCount")
      log.info(s"migrationCount $migrationCount")
      log.info(s"population ${population.size}")
      reproductionCount = 0
      migrationCount = 0
      scheduleNextStats
  }

  def migration: PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Migration, agents) =>
      migrator ! HybridMigrator.RecieveEmigrants(agents);
      List.empty
  }

  def scheduleNextStats = context.system.scheduler.scheduleOnce(1 second, self, PrintStats)

  def arenaCount[T <: Behaviour](groups: Map[T, List[Agent]], beh: T) = groups.getOrElse(beh, Seq.empty).size
}