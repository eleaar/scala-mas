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

package org.scalamas.mas.async

import org.scalamas.mas.ActorUnitSpecs
import akka.actor.ActorSystem
import akka.testkit.{TestProbe, TestActorRef}
import org.scalamas.mas.LogicTypes.Agent
import scala.concurrent.duration._
import org.scalamas.mas.async.Arena.Join
import org.scalamas.mas.async.Individual.UpdateState

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
