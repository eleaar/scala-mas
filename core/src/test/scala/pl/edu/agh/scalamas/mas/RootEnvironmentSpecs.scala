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