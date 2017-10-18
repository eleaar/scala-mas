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

package pl.edu.agh.scalamas.app.stream.graphs

import akka.stream.scaladsl.{Keep, Source}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import org.apache.commons.math3.random.RandomDataGenerator
import scala.concurrent.duration._
import org.mockito.Mockito._

class BarrierBufferFlowSpec extends StreamsSpec("BarrierBufferFlowSpec") {
  "BarrierBufferFlow" must {
    "consume and keep all elements when no flush" in {
      // given
      val bufferFlow = fixtureFlow(_ => false, expectedSize = 0)
      val sub = Source(1 to 4)
        .via(bufferFlow)
        .runWith(TestSink.probe[Int])

      // when
      sub.request(1)

      // then
      sub.expectComplete()
    }

    "don't buffer when flushing after each element" in {
      // given
      val bufferFlow = fixtureFlow(_ => true, expectedSize = 1)
      val sub = Source(1 to 4)
        .via(bufferFlow)
        .runWith(TestSink.probe[Int])

      // when
      sub.request(4)

      // then
      sub.expectNextN(1 to 4)
        .expectComplete()
    }

    "properly flushes elements" in {
      // given
      val (pub, sub) = fixtureProbes(_ % 2 == 0, expectedSize = 2)

      // then
      sub.request(3)

      pub.sendNext(1)
      sub.expectNoMsg(100.millis)

      pub.sendNext(2)
      sub.expectNext(1, 2)

      pub.sendNext(3)
      sub.expectNoMsg(100.millis)
    }

    "propagates termination after flushing is done" in {
      // given
      val (pub, sub) = fixtureProbes(_ % 2 == 0, expectedSize = 2)

      // then
      sub.request(n = 1)
      pub.sendNext(1)
      sub.expectNoMsg(100.millis)

      pub.sendNext(2)
      sub.expectNext(1)

      pub.sendComplete()
      sub.expectNoMsg(100.millis)

      sub.request(n = 1)
      sub.expectNext(2)
      sub.expectComplete()
    }

  }

  private def fixtureFlow(f: Int => Boolean, expectedSize: Int) = {
    implicit val random: RandomDataGenerator = mock[RandomDataGenerator]
    when(random.nextPermutation(expectedSize, expectedSize))
      .thenReturn((0 until expectedSize).toArray)
    BarrierBufferFlow.withoutAcc[Int](f)
  }

  private def fixtureProbes(f: Int => Boolean, expectedSize: Int) = {
    val bufferFlow = fixtureFlow(f, expectedSize)
    TestSource.probe[Int]
      .via(bufferFlow)
      .toMat(TestSink.probe[Int])(Keep.both)
      .run()
  }


}
