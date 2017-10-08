/*
 * Copyright (c) 2013 Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pl.edu.agh.scalamas.app

import org.slf4j.LoggerFactory
import pl.edu.agh.scalamas.mas.LogicStrategy
import pl.edu.agh.scalamas.mas.LogicTypes._
import pl.edu.agh.scalamas.random.RandomGeneratorComponent
import pl.edu.agh.scalamas.stats.TimeStatsComponent

import scala.collection.mutable
import scala.concurrent.duration._

/**
 * Runner for sequential apps.
 */
trait SequentialRunner extends TimeStatsComponent {
  this: AgentRuntimeComponent
    with LogicStrategy
    with RandomGeneratorComponent=>

  lazy val islandsNumber = agentRuntime.config.getInt("mas.islandsNumber")

  class Logger(loggingInterval: FiniteDuration) {
    val log = LoggerFactory.getLogger(classOf[SequentialRunner])
    val startTime = System.currentTimeMillis()
    var logDeadline = loggingInterval.fromNow

    def printOverdueLog(): Unit = {
      if (logDeadline.isOverdue()) {
        printLog
        logDeadline = loggingInterval.fromNow
      }
    }

    val reporter = createStatsReporter()

    def printLog: Unit = {
      log.info(reporter.renderCurrentValue)
    }

    log.info(reporter.renderHeaders)
    printLog
  }

  def run(duration: FiniteDuration): Unit = {
    val logger = new Logger(1.second)
    val deadline = duration.fromNow
    var islands = Array.fill(islandsNumber)(logic.initialPopulation)
    while (deadline.hasTimeLeft) {
      val migrators = mutable.ArrayBuffer.empty[Agent]
      def migration: MeetingFunction = {
        case (Migration(_), agents) =>
          migrators ++= agents
          List.empty[Agent]
      }

      islands = islands.map(island => island.groupBy(logic.behaviourFunction).flatMap(migration orElse logic.meetingsFunction).toList)

      migrators.foreach {
        agent =>
          val destination = random.nextInt(islandsNumber)
          islands(destination) ::= agent
      }

      logger.printOverdueLog()
    }
  }
}