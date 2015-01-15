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

package org.scalamas.mas

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestProbe}
import org.scalamas.mas.LogicTypes.Agent
import org.scalamas.mas.RootEnvironment.{Add, Migrate}
import org.scalacheck.Gen
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.PropertyChecks

import scala.concurrent.duration._

class RootEnvironmentSpecs extends ActorUnitSpecs(ActorSystem("RootEnvironmentSpecs")) with MockitoSugar with PropertyChecks {

  def positiveInteger = Gen.choose(1, 50)

  "A RootEnvironment actor" should {
    "forward migrating agents to its islands" in {
      forAll(positiveInteger, positiveInteger, minSuccessful(1)) {
        case ((islandsN, agentsN)) => whenever(islandsN > 0 && agentsN > 0) {
          // given
          val probe = TestProbe()
          val rootEnv = TestActorRef(RootEnvironment.props(TestActorProps.forwardingActor(probe.ref), islandsN))
          val agents = List.fill(agentsN)(mock[Agent])

          // when
          probe.send(rootEnv, Migrate(agents))

          // then
          within(100 millis) {
            probe.receiveN(agents.size) should contain theSameElementsAs (agents.map(Add(_)))
          }
        }
      }
    }
  }
}
