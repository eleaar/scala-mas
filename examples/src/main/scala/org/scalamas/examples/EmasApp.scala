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

package org.scalamas.examples

import akka.event.Logging
import org.scalamas.app.{ConcurrentAgentRuntimeComponent, ConcurrentStack, AgentRuntimeComponent}
import org.scalamas.emas.EmasLogic
import org.scalamas.genetic.RastriginProblem
import org.scalamas.mas.random.ConcurrentRandomGenerator
import org.scalamas.mas.sync.SyncEnvironment
import org.scalamas.mas.util.{Logger, Reaper}
import org.scalamas.mas.{LogicStrategy, RootEnvironment}
import org.scalamas.stats.{ConcurrentStatsFactory, StatsComponent}

import scala.concurrent.duration._

/**
 * Created by Daniel on 2015-01-12.
 */
object EmasApp {

  def main(args: Array[String]) {

    val app = new ConcurrentStack("test")
      with EmasLogic
      with RastriginProblem

    val islands = 1
    run(app, islands, 5 seconds)
  }

  def run(app: ConcurrentAgentRuntimeComponent with LogicStrategy with StatsComponent, islands: Int, duration: FiniteDuration): Unit = {

    implicit val system = app.agentRuntime.system
    import system.dispatcher

    val log = Logging(system, getClass)
    Logger(frequency = 1 second) {
      time =>
        log info (s"$time ${app.formatter(app.stats.getNow)}")
    }

    val root = system.actorOf(RootEnvironment.props(SyncEnvironment.props(app.logic), islands), "root")
    for (
      _ <- Reaper.terminateAfter(root, duration);
      _ <- app.stats.get) {
      system.shutdown()
    }
  }

}

