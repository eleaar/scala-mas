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

package pl.edu.agh.scalamas.stats

/**
 * Mixin component for application-specific statistics.
 *
 * Implementations will usually require a StatsFactory to create the initial statistics, then supply this instance to the application.
 */
trait StatsComponent {

  /**
   * An extensible reporter of application statistics. Only a single instance should be created and reused by an application runner.
   */
  def createStatsReporter(): StatsReporter

}

trait StatsReporter {

  def headers: Seq[String]


  def currentValue: Seq[String]

  /**
   * The names of the application statistics
   */
  final def renderHeaders: String = headers.mkString(" ")

  /**
   * Current value of application statistics
   */
  final def renderCurrentValue: String = currentValue.mkString(" ")
}

trait TimeStatsComponent extends StatsComponent {

  def createStatsReporter(): StatsReporter = new StatsReporter {

    private val startTime = System.currentTimeMillis()

    val headers = Seq("Time[ms]")

    def currentValue = Seq((System.currentTimeMillis() - startTime).toString)
  }
}