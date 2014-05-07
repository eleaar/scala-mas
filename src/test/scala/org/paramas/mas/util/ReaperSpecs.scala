package org.paramas.mas.util


import akka.actor._
import akka.testkit.{TestProbe, TestActorRef}
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
          Thread.sleep(duration.toMillis)

          // then
          future shouldBe 'completed
        }
      }
    }
  }

  "Method terminateAfter" should {
    "complete the future after the specified interval" in {
      //given
      import scala.concurrent.ExecutionContext.Implicits.global
      val soul = TestActorRef[MockActor]
      val duration = 100 millis

      // when
      val future = Reaper.terminateAfter(soul, duration)
      Thread.sleep(duration.toMillis * 2)

      // then
      future shouldBe 'completed
    }
  }
}


