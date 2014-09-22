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

package org.paramas.seq

import org.paramas.emas.EmasLogic
import org.paramas.emas.EmasLogic._
import org.paramas.emas.genetic.RastriginProblem
import org.paramas.emas.random.DefaultRandomGenerator

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import org.paramas.emas.config.{GeneticConfig, AppConfig}
import org.paramas.mas.Stats

object SeqApp {

  def main(args: Array[String]) {
    implicit val config = new AppConfig(ConfigFactory.load())
    implicit val stats = Stats.simple((Double.MinValue, 0L)) {
      case ((oldFitness, oldReps), (newFitness, newReps)) => (math.max(oldFitness, newFitness), oldReps + newReps)
    }
    implicit val ops: RastriginProblem = new GeneticConfig(ConfigFactory.load().getConfig("genetic")) with RastriginProblem with DefaultRandomGenerator

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