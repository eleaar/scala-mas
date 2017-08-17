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

package pl.edu.agh.scalamas.mas.async

import akka.actor.{ActorSystem, PoisonPill}
import akka.testkit.{TestActorRef, TestProbe}
import org.mockito.Mockito._
import org.scalacheck.Gen
import pl.edu.agh.scalamas.mas.LogicTypes._
import pl.edu.agh.scalamas.mas.ActorUnitSpecs
import pl.edu.agh.scalamas.mas.RootEnvironment.Add
import pl.edu.agh.scalamas.mas.async.Arena.Join
import pl.edu.agh.scalamas.mas.async.Individual.UpdateState

import scala.concurrent.duration._

class ArenaSpecs extends ActorUnitSpecs(ActorSystem("ArenaSpecs")) {

  case class fixture(sizeBefore: Int, sizeAfter: Int) {
    val populationBefore = List.fill(sizeBefore)(mock[Agent])
    val populationAfter = List.fill(sizeAfter)(mock[Agent])
    val meeting = mock[(Population) => Population]
    when(meeting.apply(populationBefore)).thenReturn(populationAfter)
  }

  def moreAfterThanBefore = for {
    before <- Gen.choose(1, 50)
    after <- Gen.choose(before + 1, 51)
  } yield (before, after)

  def lessAfterThanBefore = for {
    before <- Gen.choose(1, 50)
    after <- Gen.choose(0, before - 1)
  } yield (before, after)

  def positiveInteger = Gen.choose(1, 50)

  "An Arena actor" should {
    "perform a meeting when the arena is full" in {
      forAll(positiveInteger) {
        case (capacity) => whenever(capacity > 0) {
            // given
            val f = fixture(capacity, capacity)
            val arena = TestActorRef(Arena.props(capacity, f.meeting))

            // when
            f.populationBefore.foreach(arena ! Join(_))

            // then
            verify(f.meeting).apply(f.populationBefore)
          }
      }
    }

    "update the state of existing actor with updated agents" in {
      forAll(positiveInteger, positiveInteger) {
        case ((sizeBefore, sizeAfter)) => whenever(sizeBefore > 0 && sizeAfter > 0) {
            // given
            val f = fixture(sizeBefore, sizeAfter)
            val probe = TestProbe()
            val arena = TestActorRef(Arena.props(sizeBefore, f.meeting))

            // when
            f.populationBefore.foreach {
              agent => probe.send(arena, Join(agent))
            }

            // then
            f.populationAfter take math.min(sizeBefore, sizeAfter) foreach {
              agent => probe.expectMsg(100.millis, UpdateState(agent))
            }
          }
      }
    }

    "spawn new actors for new agents" in {
      forAll(moreAfterThanBefore) {
        case ((sizeBefore, sizeAfter)) => whenever(sizeBefore > 0 && sizeAfter > sizeBefore) {
            // given
            val f = fixture(sizeBefore, sizeAfter)
            val probe = TestProbe()
            val arena = TestActorRef(Arena.props(sizeBefore, f.meeting), probe.ref, "arena")

            // when
            f.populationBefore.foreach(arena ! Join(_))

            // then
            f.populationAfter.drop(sizeBefore) foreach {
              agent => probe.expectMsg(100.millis, Add(agent))
            }
          }
      }
    }

    "kill actors for dead agents" in {
      forAll(lessAfterThanBefore) {
        case ((sizeBefore, sizeAfter)) => whenever(sizeBefore > 0 && sizeAfter >= 0 && sizeAfter < sizeBefore) {
            // given
            val f = fixture(sizeBefore, sizeAfter)
            val probe = TestProbe()
            val arena = TestActorRef(Arena.props(sizeBefore, f.meeting), probe.ref, "arena")

            // when
            f.populationBefore.foreach(arena ! Join(_))

            // then
            f.populationAfter.drop(sizeAfter) foreach {
              agent => probe.expectMsg(100.millis, PoisonPill)
            }
          }
      }
    }
  }

}