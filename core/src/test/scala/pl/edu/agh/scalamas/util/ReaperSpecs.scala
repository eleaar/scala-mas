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
package org.scalamas.util


import akka.actor._
import akka.testkit.{TestActorRef, TestProbe, _}
import org.mockito.Mockito._
import org.scalacheck.Gen
import org.scalamas.mas.{ActorUnitSpecs, MockActor}
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.PropertyChecks

import scala.concurrent.duration._

class ReaperSpecs extends ActorUnitSpecs(ActorSystem("ReaperSpecs")) with MockitoSugar with PropertyChecks {

  def positiveInteger = Gen.choose(1, 50)

  "A Reaper actor" should {
    "execute callback early when given an empty initial list of souls" in {
      // given
      val trigger = mock[() => Unit]
      val souls = List.empty[ActorRef]

      // when
      TestActorRef(new Reaper(souls, trigger))

      // then
      verify(trigger).apply()
    }

    "execute callback when all souls are terminated" in {
      forAll(positiveInteger) {
        case (count) => whenever(count > 0) {
          // given
          val trigger = mock[() => Unit]
          val souls = List.fill(count)(TestActorRef[MockActor])
          val reaper = TestActorRef(new Reaper(souls, trigger))

          // when
          souls.foreach(_ ! PoisonPill)

          // then
          verify(trigger).apply()
        }
      }
    }

    "stop after callback has been executed" in {
      // given
      val trigger = mock[() => Unit]
      val souls = List.empty[ActorRef]
      val probe = TestProbe()

      // when
      val reaper = TestActorRef(new Reaper(souls, trigger))
      probe watch reaper

      // then
      probe.expectTerminated(reaper)
    }
  }

  "Method actorsTerminate" should {
    "complete the future when its argument actors are terminated" in {
      forAll(positiveInteger) {
        case (count) => whenever(count > 0) {
          //given
          val souls = List.fill(count)(TestActorRef[MockActor])
          val future = Reaper.actorsTerminate(souls)
          val duration = 100 millis

          // when
          souls.foreach(_ ! PoisonPill)
          Thread.sleep(duration.dilated.toMillis)

          // then
          future shouldBe 'completed
        }
      }
    }
  }

  "Method terminateAfter" should {
    "complete the future after the specified interval" in {
      //given
      import system.dispatcher
      val soul = TestActorRef[MockActor]
      val duration = 100 millis

      // when
      val future = Reaper.terminateAfter(soul, duration)
      Thread.sleep(duration.dilated.toMillis)

      // then
      future shouldBe 'completed
    }
  }
}