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

import com.typesafe.config.Config
import org.paramas.emas.genetic.{SteepestDescend, LabsProblem, GeneticOps, RastriginProblem}
import org.paramas.emas.random.DefaultRandomGenerator
import org.paramas.mas.util.{Logger, Reaper}
import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._
import akka.event.Logging
import scala.concurrent.ExecutionContext.Implicits.global
import org.paramas.mas.{Stats, Logic, RootEnvironment}
import org.paramas.emas.config.{GeneticConfig, AppConfig}
import org.paramas.mas.async.AsyncEnvironment
import org.paramas.mas.sync.SyncEnvironment

trait RastriginConfig {
  def ops(c: Config): RastriginProblem = new GeneticConfig(c) with RastriginProblem with DefaultRandomGenerator
}

object Async extends EmasApp with RastriginConfig {

  def main(args: Array[String]) {
    run("concurrent", ops, AsyncEnvironment.props, 15 minutes)
  }
}

object Sync extends EmasApp with RastriginConfig {

  def main(args: Array[String]) {
    run("hybrid", ops, SyncEnvironment.props, 15 minutes)
  }
}

class EmasApp {

  def run[G <: GeneticOps[G]](name: String, opsF: (Config) => G, islandsProps: (Logic) => Props, duration: FiniteDuration) {

    implicit val system = ActorSystem(name)
    implicit val settings = AppConfig(system)
    implicit val ops: LabsProblem = new GeneticConfig(system.settings.config.getConfig("genetic")) with LabsProblem with SteepestDescend with DefaultRandomGenerator
//    implicit val ops: G = opsF(system.settings.config.getConfig("genetic"))

    implicit val stats = Stats.concurrent((Double.MinValue, 0L)) {
      case ((oldFitness, oldReps), (newFitness, newReps)) => (math.max(oldFitness, newFitness), oldReps + newReps)
    }

    val log = Logging(system, getClass)
    Logger(frequency = 1 second) {
      time =>
        val (fitness, reproductions) = stats.getNow
        log info (s"fitness $time $fitness")
        log info (s"reproductions $time $reproductions")
    }

    val root = system.actorOf(RootEnvironment.props(islandsProps(new EmasLogic), settings.emas.islandsNumber), "root")
    for (
      _ <- Reaper.terminateAfter(root, duration);
      _ <- stats.get) {
      system.shutdown()
    }
  }
}
