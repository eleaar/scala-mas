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

import akka.actor.Props
import pl.edu.agh.scalamas.mas.LogicStrategy
import pl.edu.agh.scalamas.mas.async.AsyncEnvironment
import pl.edu.agh.scalamas.mas.sync.SyncEnvironment

/**
 * Strategy component for the granularity of agent concurrency.
 */
trait EnvironmentStrategy {

  def environmentProps: Props
}

/**
 * Strategy for coarse-grained agent concurrency.
 */
trait SynchronousEnvironment extends EnvironmentStrategy {
  this: LogicStrategy =>

  def environmentProps = SyncEnvironment.props(logic)
}

/**
 * Strategy for fine-grained agent concurrency.
 */
trait AsynchronousEnvironment extends EnvironmentStrategy {
  this: LogicStrategy =>

  def environmentProps = AsyncEnvironment.props(logic)
}