package org.paramas.mas.util


import akka.actor._
import akka.testkit.{TestProbe, TestActorRef}
import org.paramas.mas.ActorUnitSpecs
import org.scalatest.mock.MockitoSugar
import scala.concurrent.duration._

class MockActor extends Actor {
  def receive = {
    case _ =>
  }
}

class ReaperSpecs extends ActorUnitSpecs(ActorSystem("ReaperSpecs")) with MockitoSugar {

  def fixture =
    new {
      var triggered = 0

      def trigger = () => triggered += 1
    }

  "A Reaper actor" should {
    "execute callback early when given an empty initial list of souls" in {
      // given
      val f = fixture
      val souls = List.empty[ActorRef]

      // when
      TestActorRef(new Reaper(souls, f.trigger))

      // then
      f.triggered shouldBe 1
    }

    "execute callback when all souls are terminated" in {
      // given
      val f = fixture
      val souls = List.fill(3)(TestActorRef[MockActor])
      val reaper = TestActorRef(new Reaper(souls, f.trigger))

      // when
      souls.foreach(_ ! PoisonPill)

      // then
      f.triggered shouldBe 1
    }

    "stop after callback has been executed" in {
      // given
      val f = fixture
      val souls = List.empty[ActorRef]
      val probe = TestProbe()

      // when
      val reaper = TestActorRef(new Reaper(souls, f.trigger))
      probe watch reaper

      // then
      probe.expectTerminated(reaper)
    }
  }

  "Method actorsTerminate" should {
    "complete the future when its argument actors are terminated" in {
      //given
      val souls = List.fill(3)(TestActorRef[MockActor])
      val future = Reaper.actorsTerminate(souls)

      // when
      souls.foreach(_ ! PoisonPill)

      // then
      future shouldBe 'completed
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


