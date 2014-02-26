package com.krzywicki.hybrid

import com.krzywicki.util.Config
import com.krzywicki.util.Genetic._
import com.krzywicki.util.MAS._
import akka.actor._
import scala.concurrent.duration._
import com.krzywicki.util.Logger

object HybridIsland {
  case class Start(migrator: ActorRef, logger: Logger)
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
  var logger: Logger = _

  def getBestFitness(population: Population) = population.maxBy(_.fitness).fitness
  var bestFitness: Fitness = Double.MinValue
  var reproductionCount = 0

  def receive = {
    case Start(_migrator, _logger) =>
      migrator = _migrator
      logger = _logger
      bestFitness = getBestFitness(population)

      migrator ! HybridMigrator.RegisterIsland(self)
      scheduleNextStats
      self ! Loop

    case Loop =>
      val arenas = population.groupBy(behaviour)
      population = arenas.flatMap(migration orElse meetings).toList

      reproductionCount += arenaCount(arenas, Reproduction)
      bestFitness = math.max(bestFitness, getBestFitness(population))
      self ! Loop

    case Migrate(a) => population :+= a

    case PrintStats =>
      capture(bestFitness)(f => logger.fitness send (math.max(_, f)))
      capture(reproductionCount)(r => logger.reproduction send (_ + r))
      reproductionCount = 0
      scheduleNextStats
  }

  def migration: PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Migration, agents) =>
      migrator ! HybridMigrator.RecieveEmigrants(agents);
      List.empty
  }

  def scheduleNextStats = context.system.scheduler.scheduleOnce(1 second, self, PrintStats)

  def arenaCount[T <: Behaviour](groups: Map[T, List[Agent]], beh: T) = groups.getOrElse(beh, Seq.empty).size
  
  def capture[T](t: T)(body: T => Unit) = body(t)
}