package pl.edu.agh.scalamas.app

import org.slf4j.LoggerFactory
import pl.edu.agh.scalamas.mas.LogicStrategy
import pl.edu.agh.scalamas.mas.LogicTypes._
import pl.edu.agh.scalamas.random.RandomGeneratorComponent
import pl.edu.agh.scalamas.stats.StatsComponent

import scala.collection.mutable
import scala.concurrent.duration._

/**
 * Runner for sequential apps.
 */
trait SequentialRunner {
  this: AgentRuntimeComponent
    with LogicStrategy
    with StatsComponent
    with RandomGeneratorComponent=>

  lazy val islandsNumber = agentRuntime.config.getInt("mas.islandsNumber")

  class Logger(loggingInterval: FiniteDuration) {
    val log = LoggerFactory.getLogger(classOf[SequentialRunner])
    val startTime = System.currentTimeMillis()
    var logDeadline = loggingInterval fromNow

    def printOverdueLog(): Unit = {
      if (logDeadline.isOverdue()) {
        printLog
        logDeadline = loggingInterval fromNow
      }
    }

    def printLog: Unit = {
      val time = System.currentTimeMillis() - startTime
      log info (s"$time ${formatter(stats.getNow)}")
    }

    printLog
  }

  def run(duration: FiniteDuration): Unit = {
    val logger = new Logger(1 second)
    val deadline = duration fromNow
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
