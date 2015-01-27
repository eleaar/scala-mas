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
package pl.edu.agh.scalamas.util

import org.apache.commons.math3.random.RandomDataGenerator

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.ArrayBuffer

object Util {

  implicit class Shuffled[T, CC[X] <: TraversableOnce[X]](xs: CC[T]) {

    def shuffled(implicit rand: RandomDataGenerator, bf: CanBuildFrom[CC[T], T, CC[T]]): CC[T] = {
      val buf = new ArrayBuffer[T] ++= xs
      val size = buf.size
      val perm = rand.nextPermutation(size, size)
      permutate(buf, perm)
      (bf(xs) ++= buf).result()
    }

    private[util] def permutate(data: ArrayBuffer[T], perm: Array[Int]) = {
      for (i <- 0 until data.size) {
        val x = data(i)
        var current = i
        var next = perm(i)
        perm(i) = i

        while (next != i) {
          data(current) = data(next)
          current = next
          next = perm(current)
          perm(current) = current
        }
        data(current) = x
      }
    }
  }

}