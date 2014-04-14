package org.paramas.mas

import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.ActorSystem

/**
 * Created by krzywick on 2014-04-14.
 */
abstract class ActorUnitSpecs(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll{

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}
