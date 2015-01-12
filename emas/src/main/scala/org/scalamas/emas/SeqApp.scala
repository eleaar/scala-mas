package org.scalamas.emas

import com.typesafe.config.ConfigFactory
import org.scalamas.emas.config.{AppConfig, GeneticConfig}
import org.scalamas.mas.random.DefaultRandomGenerator
import org.scalamas.mas.LogicTypes.{MeetingFunction, Agent, Migration}
import org.scalamas.stats.Stats
import org.scalamas.genetic.RastriginProblem

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.util.Random

object SeqApp extends RastriginConfig {

  def main(args: Array[String]) {
    val c = ConfigFactory.load()
    implicit val config = new AppConfig(c)
    val stats = Stats.simple((-10000.0, 0L)) {
      case ((oldFitness, oldReps), (newFitness, newReps)) => (math.max(oldFitness, newFitness), oldReps + newReps)
    }

    val deadline = time fromNow
    val startTime = System.currentTimeMillis()
    var logDeadline = 1 second fromNow
    def log() = {
      val time = System.currentTimeMillis() - startTime
      val (fitness, reproductions) = stats.getNow
      println(s"fitness $time $fitness")
      println(s"reproductions $time $reproductions")
    }
    log()

    val logic = new EmasLogic[RastriginProblem](ops(c.getConfig("genetic")), stats, config)
    import logic._

    val islandsNumber = config.emas.islandsNumber
    var islands = Array.fill(islandsNumber)(initialPopulation)

    while (deadline.hasTimeLeft) {
      val migrators = ArrayBuffer.empty[Agent]
      def migration: MeetingFunction = {
        case (Migration(_), agents) =>
          migrators ++= agents
          List.empty[Agent]
      }

      islands = islands.map(island => island.groupBy(behaviourFunction).flatMap(migration orElse meetingsFunction).toList)

      migrators.foreach {
        agent =>
          val destination = Random.nextInt(islandsNumber)
          islands(destination) ::= agent
      }

      if(logDeadline.isOverdue()){
        log()
        logDeadline = 1 second fromNow
      }
    }


  }


}
