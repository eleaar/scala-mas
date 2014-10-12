/**
 * Copyright (C) 2013 - 2014, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * This file is part of ParaphraseAGH/Scala.
 *
 * ParaphraseAGH/Scala is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ParaphraseAGH/Scala is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ParaphraseAGH/Scala.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.paramas.emas

import com.typesafe.config.ConfigFactory
import org.paramas.emas.config.{AppConfig, GeneticConfig}
import org.paramas.emas.genetic.RastriginProblem
import org.paramas.emas.random.DefaultRandomGenerator
import org.paramas.mas.LogicTypes.{MeetingFunction, Agent, Migration}
import org.paramas.mas.Stats

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