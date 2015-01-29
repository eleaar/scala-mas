/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.edu.agh.scalamas.random

import org.apache.commons.math3.random.RandomDataGenerator
import pl.edu.agh.scalamas.app.AgentRuntimeComponent

import scala.util.Random

/**
 * Random Generator Component that can be used in concurrent applications.
 * Separate RNG are kept in thread locals for every accessing thread.
 *
 * They are seeded from a separate, shared RNG at first access. Therefore, the results of the application
 * can be repeatable if the thread access pattern to the RNGs are repeatable.
 */
trait ConcurrentRandomGeneratorComponent extends RandomGeneratorComponent {
  this: AgentRuntimeComponent =>

  def randomData = ConcurrentRandomGeneratorComponent.current

  object ConcurrentRandomGeneratorComponent {
    private[this] val seedSource = new Random(globalSeed)

    private[this] val localRandom = new ThreadLocal[RandomDataGenerator] {
      override protected def initialValue() = new RandomDataGenerator(randomGeneratorFactory(seedSource.nextLong()))
    }

    def current = localRandom.get
  }

}