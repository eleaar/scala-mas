package org.paramas.mas

import akka.actor.ActorSystem
import org.scalatest.mock.MockitoSugar
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import akka.testkit.{TestProbe, TestActorRef}
import org.paramas.mas.LogicTypes.Agent
import org.paramas.mas.RootEnvironment.{Add, Migrate}
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
