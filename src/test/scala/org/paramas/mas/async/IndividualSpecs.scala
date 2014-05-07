package org.paramas.mas.async

import org.paramas.mas.ActorUnitSpecs
import akka.actor.ActorSystem
import akka.testkit.{TestProbe, TestActorRef}
import org.paramas.mas.LogicTypes.Agent
import scala.concurrent.duration._
import org.paramas.mas.async.Arena.Join
import org.paramas.mas.async.Individual.UpdateState

class IndividualSpecs extends ActorUnitSpecs(ActorSystem("IndividualSpecs")) {
  "An Individual actor" should {
    "choose an join an arena on start" in {
      // given
      val state = mock[Agent]
      val probe = TestProbe()
      val behaviour = (agent: Agent) => probe.ref

      // when
      val individual = TestActorRef(Individual.props(state, behaviour))

      // then
      probe.expectMsg(100 millis, Join(state))
    }

    "update state and join new arena" in {
      // given
      val oldState = mock[Agent]
      val newState = mock[Agent]
      val oldArena = TestProbe()
      val newArena = TestProbe()
      val behaviour = (agent: Agent) => agent match {
        case `oldState` => oldArena.ref
        case `newState` => newArena.ref
      }
      val individual = TestActorRef(Individual.props(oldState, behaviour))

      // when
      individual ! UpdateState(newState)

      // then
      newArena.expectMsg(100 millis, Join(newState))
    }
  }
}
