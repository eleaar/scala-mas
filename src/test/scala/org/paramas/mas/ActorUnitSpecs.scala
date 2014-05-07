package org.paramas.mas

import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.{Props, ActorRef, Actor, ActorSystem}
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.PropertyChecks

object TestActorProps {
  def mockActor = Props[MockActor]

  def forwardingActor(next: ActorRef) = Props(classOf[ForwardingActor], next)
}

class MockActor extends Actor {
  def receive = {
    case _ =>
  }
}

class ForwardingActor(next: ActorRef) extends Actor {
  def receive = {
    case x =>  next.forward(x)
  }
}

abstract class ActorUnitSpecs(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar with PropertyChecks {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}
