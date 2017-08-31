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

import pl.edu.agh.scalamas.app.stream.StreamingStack
import pl.edu.agh.scalamas.emas.EmasLogic
import pl.edu.agh.scalamas.genetic.RastriginProblem
import pl.edu.agh.scalamas.mas.stream._
import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration._

object ContinuousStreamingApp extends StreamingStack("ContinuousStreamingApp")
  with ContinuousStreamingStrategy
  with EmasLogic
  with RastriginProblem {

  def main(args: Array[String]): Unit = {
    run(agentRuntime.config.as[FiniteDuration]("duration"))
  }
}

object SequentialStreamingApp extends StreamingStack("SequentialStreamingApp")
  with SequentialStreamingStrategy
  with EmasLogic
  with RastriginProblem {

  def main(args: Array[String]): Unit = {
    run(agentRuntime.config.as[FiniteDuration]("duration"))
  }
}

object SynchronousStreamingApp extends StreamingStack("SynchronousStreamingApp")
  with SynchronousStreamingStrategy
  with EmasLogic
  with RastriginProblem {

  def main(args: Array[String]): Unit = {
    run(agentRuntime.config.as[FiniteDuration]("duration"))
  }
}