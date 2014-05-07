package org.paramas.mas.async

import org.paramas.mas.ActorUnitSpecs
import akka.actor.{PoisonPill, ActorSystem}
import akka.testkit.{TestProbe, TestActorRef}
import org.scalatest.mock.MockitoSugar
import org.paramas.mas.LogicTypes._
import org.paramas.mas.async.Arena.Join
import org.mockito.Mockito._
import org.paramas.mas.async.Individual.UpdateState
import org.paramas.mas.RootEnvironment.Add
import org.scalatest.prop.PropertyChecks
import org.scalacheck.Gen
import scala.concurrent.duration._

class ArenaSpecs extends ActorUnitSpecs(ActorSystem("ArenaSpecs")) {

  def fixture(sizeBefore: Int, sizeAfter: Int) =
    new {
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
            f.populationAfter take (math.min(sizeBefore, sizeAfter)) foreach {
              agent => probe.expectMsg(100 millis, UpdateState(agent))
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
              agent => probe.expectMsg(100 millis, Add(agent))
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
              agent => probe.expectMsg(100 millis, PoisonPill)
            }
          }
      }
    }
  }

}
