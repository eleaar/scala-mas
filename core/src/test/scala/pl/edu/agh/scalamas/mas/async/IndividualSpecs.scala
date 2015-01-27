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
package pl.edu.agh.scalamas.mas.async

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestProbe}
import pl.edu.agh.scalamas.mas.LogicTypes.Agent
import pl.edu.agh.scalamas.mas.ActorUnitSpecs
import pl.edu.agh.scalamas.mas.async.Arena.Join
import pl.edu.agh.scalamas.mas.async.Individual.UpdateState

import scala.concurrent.duration._

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