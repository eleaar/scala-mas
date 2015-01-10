/**
 * Copyright (C) 2013 - 2014, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * This file is part of ParaphraseAGH/Scala.
 *
 * ParaphraseAGH/Scala is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ParaphraseAGH/Scala is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ParaphraseAGH/Scala.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.paramas.mas.util


import akka.actor._
import akka.testkit.{TestProbe, TestActorRef}
import akka.testkit._
import org.paramas.mas.{MockActor, ActorUnitSpecs}
import org.scalatest.mock.MockitoSugar
import scala.concurrent.duration._
import org.mockito.Mockito._
import org.scalatest.prop.PropertyChecks
import org.scalacheck.Gen

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


