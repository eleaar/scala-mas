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

package pl.edu.agh.scalamas.util

import java.util

import org.apache.commons.math3.random.RandomDataGenerator

case class RandomBuffer[T]() {
  private val buf = new util.ArrayList[T]()

  def add(element: T): Unit = {
    buf.add(element)
  }

  def removeRandom()(implicit random: RandomDataGenerator): Option[T] = {
    val size = buf.size()
    if (size > 0) {
      val lastIndex = size - 1
      val chosenIndex = random.nextInt(0, lastIndex) // inclusive
      val chosenElement = buf.get(chosenIndex)
      buf.set(chosenIndex, buf.get(lastIndex))
      buf.remove(lastIndex)
      Some(chosenElement)
    } else {
      None
    }
  }

  def size: Int = buf.size()

  def isEmpty: Boolean = buf.isEmpty

  def nonEmpty: Boolean = !buf.isEmpty
}
