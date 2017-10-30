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

package pl.edu.agh.scalamas.examples

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import pl.edu.agh.scalamas.app.AgentRuntimeComponent
import pl.edu.agh.scalamas.app.stream.StreamingStack
import pl.edu.agh.scalamas.emas.EmasTypes.Agent
import pl.edu.agh.scalamas.emas.stats.{EmasStreamingGenerationStats, EmasStreamingIterationStats}
import pl.edu.agh.scalamas.emas.stream.EmergencyLoggingStream
import pl.edu.agh.scalamas.emas.{EmasLogic, EmasOrderings}
import pl.edu.agh.scalamas.examples.TemporaryConfigurationLogging._
import pl.edu.agh.scalamas.genetic.RastriginProblem
import pl.edu.agh.scalamas.mas.LogicTypes
import pl.edu.agh.scalamas.mas.stream._
import pl.edu.agh.scalamas.mas.stream.buffer.{AnnealedShufflingStrategy, BarrierBufferStrategy, ShufflingBufferStrategy}

import scala.concurrent.duration._

object TemporaryConfigurationLogging{
  def logPaths(paths: String*): Unit = {
    val config = ConfigFactory.load()
    paths.foreach { path =>

      println(s"$path: ${config.getString(path)}")
    }
  }
}

trait EmasLoopingBufferSize { this: AgentRuntimeComponent =>
  protected final val loopingBufferSize = {
    agentRuntime.config.as[Int]("emas.populationSize") *
    agentRuntime.config.as[Int]("emas.initialEnergy")
  }
}

object ContinuousStreamingApp extends StreamingStack("ContinuousStreamingApp")
  with ContinuousStreamingStrategy
  with ShufflingBufferStrategy
  with EmasLogic
  with EmasStreamingGenerationStats
  with EmasStreamingIterationStats
  with EmasLoopingBufferSize
  with EmergencyLoggingStream
  with RastriginProblem {


  protected def agentOrdering = Ordering.fromLessThan[LogicTypes.Agent] {
    case (a: Agent[Genetic], b: Agent[Genetic]) =>
      genetic.ordering.lt(a.fitness, b.fitness)
    case (a, b) => a.hashCode() < b.hashCode()
  }

  def main(args: Array[String]): Unit = {
    println("name: ContinuousStreamingApp")
    logPaths(
      "emas.populationSize",
      "streaming.arenas.parallelism",
      "streaming.continuous.shuffling-buffer-size"
    )
    println()
    println()
    run(agentRuntime.config.as[FiniteDuration]("duration"))
  }
}

object ContinuousAnnealedStreamingApp extends StreamingStack("ContinuousAnnealedStreamingApp")
  with ContinuousStreamingStrategy
  with AnnealedShufflingStrategy
  with EmasLogic
  with EmasOrderings
  with EmasStreamingGenerationStats
  with EmasStreamingIterationStats
  with EmasLoopingBufferSize
  with EmergencyLoggingStream
  with RastriginProblem {

  protected def agentOrdering = agentOrderings.onFitness

  def main(args: Array[String]): Unit = {
    println("name: ContinuousAnnealedStreamingApp")
    logPaths(
      "emas.populationSize",
      "streaming.arenas.parallelism",
      "streaming.continuous.shuffling-buffer-size",
      "streaming.continuous.halfDecayInSeconds"
    )
    println()
    run(agentRuntime.config.as[FiniteDuration]("duration"))
  }
}

object ContinuousBarrierStreamingApp extends StreamingStack("ContinuousBarrierStreamingApp")
  with ContinuousStreamingStrategy
  with BarrierBufferStrategy
  with EmasLogic
  with EmasOrderings
  with EmasStreamingGenerationStats
  with EmasStreamingIterationStats
  with EmasLoopingBufferSize
  with EmergencyLoggingStream
  with RastriginProblem {

  protected val expectedTotal = {
    val populationSize = agentRuntime.config.getInt("emas.populationSize")
    val initialEnergy = agentRuntime.config.getInt("emas.initialEnergy")
    populationSize * initialEnergy
  }

  protected def weight(agent: LogicTypes.Agent) = agent match {
    case a: Agent[Genetic] => a.energy
    case _ => 0
  }

  def main(args: Array[String]): Unit = {
    println("name: ContinuousBarrierStreamingApp")
    logPaths(
      "emas.populationSize",
      "streaming.arenas.parallelism",
    )
    println()
    println()
    println()
    run(agentRuntime.config.as[FiniteDuration]("duration"))
  }
}

object SequentialStreamingApp extends StreamingStack("SequentialStreamingApp")
  with SequentialStreamingStrategy
  with EmasLogic
  with EmasStreamingGenerationStats
  with EmasStreamingIterationStats
  with EmasLoopingBufferSize
  with RastriginProblem {

  def main(args: Array[String]): Unit = {
    println("name: SequentialStreamingApp")
    logPaths(
      "emas.populationSize",
      "streaming.arenas.parallelism"
    )
    println()
    println()
    println()
    run(agentRuntime.config.as[FiniteDuration]("duration"))
  }
}

object SynchronousStreamingApp extends StreamingStack("SynchronousStreamingApp")
  with SynchronousStreamingStrategy
  with EmasLogic
  with EmasStreamingGenerationStats
  with EmasStreamingIterationStats
  with EmasLoopingBufferSize
  with RastriginProblem {

  def main(args: Array[String]): Unit = {
    println("name: SynchronousStreamingApp")
    logPaths(
      "emas.populationSize"
    )
    println()
    println()
    println()
    println()
    run(agentRuntime.config.as[FiniteDuration]("duration"))
  }
}