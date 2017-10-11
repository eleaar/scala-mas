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
 * Interface for the gathering of statistics during computations. The statistics value is the result of folding in time all the provided updates.
 * @tparam T - compound type of the gathered statistics.
 */
trait Stats[T] {
  /**
   * Updates the value of the statistic in some point in the future.
   */
  def update(newValue: T)

  /**
   * Synchronously returns the current value of the statistics (may not reflect pending udpates).
   */
  def get: T
}

object UnitStats extends Stats[Unit] {
  def update(newValue: Unit): Unit = {}

  def get: Unit = ()
}

case class LongStats(initialValue: Long, updateFunction: (Long, Long) => Long) extends Stats[Long] {

  private val accumulator = LongAccumulator(initialValue, updateFunction)

  def update(newValue: Long): Unit = accumulator.accumulate(newValue)

  def get: Long = accumulator.get()
}

case class DoubleStats(initialValue: Double, updateFunction: (Double, Double) => Double) extends Stats[Double] {

  private val accumulator = DoubleAccumulator(initialValue, updateFunction)

  def update(newValue: Double): Unit = accumulator.accumulate(newValue)

  def get: Double = accumulator.get()
}

/**
 * Combines a tuple of stats into a stats of tuples. The resulting stats will be threadsafe but not atomic.
 */
case class TupleStats[A, B](
  left: Stats[A],
  right: Stats[B]
) extends Stats[(A, B)] {

  def update(newValue: (A, B)): Unit = {
    left.update(newValue._1)
    right.update(newValue._2)
  }

  def get: (A, B) = (left.get, right.get)
}

trait HasStats[T] {
  def apply(initialValue: T, updateFunction: (T, T) => T): Stats[T]
}

object HasStats {

  implicit val longInstance: HasStats[Long] = new HasStats[Long] {
    def apply(initialValue: Long, updateFunction: (Long, Long) => Long) = LongStats(initialValue, updateFunction)
  }

  implicit val doubleInstance: HasStats[Double] = new HasStats[Double] {
    def apply(initialValue: Double, updateFunction: (Double, Double) => Double) = DoubleStats(initialValue, updateFunction)
  }

  /**
   * Assumes the update function is an independent product of two update functions
   */
  implicit def tupleInstance[A, B](implicit A: HasStats[A], B: HasStats[B]): HasStats[(A, B)] = new HasStats[(A, B)] {
    def apply(initialValue: (A, B), updateFunction: ((A, B), (A, B)) => (A, B)) = {
       val initialA = initialValue._1
       val initialB = initialValue._2

       val aStats = A(initialValue._1, (a1, a2) => updateFunction((a1, initialB), (a2, initialB))._1)
       val bStats = B(initialValue._2, (b1, b2) => updateFunction((initialA, b1), (initialA, b2))._2)

      TupleStats(aStats, bStats)
    }
  }
}
