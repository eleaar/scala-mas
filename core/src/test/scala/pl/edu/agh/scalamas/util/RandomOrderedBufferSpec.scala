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

import org.apache.commons.math3.random.RandomDataGenerator
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpecLike}

import scala.util.Random

class RandomOrderedBufferSpec extends WordSpecLike with Matchers with MockitoSugar {

  implicit private val random = new RandomDataGenerator()

  "RandomOrderedBuffer" must {

    "select single element as minimum" in {
      // given
      val element = 1
      val buffer = new RandomOrderedBuffer[Int]

      //  when
      buffer.add(element)

      // then
      buffer.removeMax() shouldBe Some(element)
    }

    "select single element as random" in {
      // given
      val element = 1
      val buffer = new RandomOrderedBuffer[Int]

      //  when
      buffer.add(element)

      // then
      buffer.removeRandom() shouldBe Some(element)
    }

    "select random element" in {
      // given
      val input = 1 to 10
      val buffer = new RandomOrderedBuffer[Int]

      for(_ <- 1 to 100) {
        // when
        input.foreach(buffer.add)
        val result = Seq.fill(input.size)(buffer.removeRandom()).flatten

        // then
        result should contain theSameElementsAs input
        buffer.removeRandom() shouldBe None
      }
    }

    "select minimum element" in {
      // given
      val input = 1 to 10
      val scalaRandom = new Random
      val buffer = new RandomOrderedBuffer[Int]

      for(_ <- 1 to 100) {
        // when
        scalaRandom.shuffle(input).foreach(buffer.add)
        val result = Seq.fill(input.size)(buffer.removeMax()).flatten

        // then
        result should contain theSameElementsInOrderAs input.reverse
        buffer.removeRandom() shouldBe None
      }
    }

  }

}
