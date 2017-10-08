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

package pl.edu.agh.scalamas.emas

import com.codahale.metrics.MetricRegistry.MetricSupplier
import com.codahale.metrics.{Reservoir, SlidingTimeWindowArrayReservoir, SlidingWindowReservoir}
import net.ceedubs.ficus.Ficus._
import nl.grons.metrics.scala.{DefaultInstrumented, Histogram}
import pl.edu.agh.scalamas.app.AgentRuntimeComponent
import pl.edu.agh.scalamas.stats.{StatsComponent, StatsReporter}

import scala.concurrent.duration.FiniteDuration


trait AgentGenerationStats extends StatsComponent { this: AgentRuntimeComponent =>

  private def withReservoir(reservoir: => Reservoir) = {
    new MetricSupplier[com.codahale.metrics.Histogram] {
      def newMetric() = new com.codahale.metrics.Histogram(reservoir)
    }
  }

  private val enabled = agentRuntime.config.as[Boolean]("stats.log-generations")
  private val statsFrequency = agentRuntime.config.as[FiniteDuration]("stats.frequency")
  private val populationSize = agentRuntime.config.as[Int]("emas.populationSize")

  private object GenerationMetrics extends DefaultInstrumented

  val timedGenerationHistogram = new Histogram(
    GenerationMetrics.metricRegistry.histogram(
      "timedGenerationHistogram", withReservoir(
        new SlidingTimeWindowArrayReservoir(statsFrequency.length, statsFrequency.unit)
      )
    )
  )
  val sizedGenerationHistogram = new Histogram(
    GenerationMetrics.metricRegistry.histogram(
      "sizedGenerationHistogram", withReservoir(
        new SlidingWindowReservoir(populationSize)
      )
    )
  )

  abstract override def createStatsReporter(): StatsReporter = {
    val parent = super.createStatsReporter()
    if (enabled) {
      new StatsReporter {

        val headers: Seq[String] = {
          parent.headers ++ Seq(
            "timedGenerationMin",
            "timedGenerationMax",
            "timedGenerationMean",
            "sizedGeneationMin",
            "sizedGeneationMax",
            "sizedGeneationMean",
          )
        }

        def currentValue: Seq[String] = {
          val timedSnapshot = timedGenerationHistogram.snapshot
          val sizedSnapshot = sizedGenerationHistogram.snapshot
          parent.currentValue ++ Seq(
            timedSnapshot.getMin.toString,
            timedSnapshot.getMax.toString,
            timedSnapshot.getMean.round.toString,
            sizedSnapshot.getMin.toString,
            sizedSnapshot.getMax.toString,
            sizedSnapshot.getMean.round.toString
          )
        }
      }
    } else {
      parent
    }
  }

}
