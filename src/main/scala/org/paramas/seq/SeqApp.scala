package org.paramas.seq

import org.paramas.emas.EmasLogic
import org.paramas.emas.EmasLogic._

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import org.paramas.emas.config.AppConfig
import org.paramas.mas.Stats

object SeqApp {

  def main(args: Array[String]) {
    implicit val config = new AppConfig(ConfigFactory.load())
    implicit val stats = Stats.simple((Double.MinValue, 0L)) {
      case ((oldFitness, oldReps), (newFitness, newReps)) => (math.max(oldFitness, newFitness), oldReps + newReps)
    }

    val deadline = 10 seconds fromNow

    val logic = new EmasLogic
    import logic._

    var population = initialPopulation
    while (deadline.hasTimeLeft) {
      population = population.groupBy(behaviourFunction).flatMap(meetingsFunction).toList
      println(stats.getNow)
    }
  }
}