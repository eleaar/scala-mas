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
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalacheck.Gen
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, WordSpecLike}

class RandomBufferSpec extends WordSpecLike with Matchers with MockitoSugar {

  "RandomBuffer" must {

    "return no element when empty" in {
      implicit val random = mock[RandomDataGenerator]
      val buffer = RandomBuffer[Int]()
      buffer.removeRandom() shouldBe empty
    }

    "remove element in the given order, then be empty" in {
      // given
      implicit val random = mock[RandomDataGenerator]
      val buffer = RandomBuffer[Int]()

      // when
      (1 to 10).foreach(buffer.add)

      // then
      buffer.size shouldBe 10

      // when
      when(random.nextInt(any[Int], any[Int]))
        .thenReturn(9, (0 to 8).reverse: _*)
      val result = Seq.fill(10)(buffer.removeRandom()).flatten

      // then
      result shouldBe (1 to 10).reverse
      buffer.size shouldBe 0
      buffer.removeRandom() shouldBe empty
    }


  }

}
