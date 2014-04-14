package org.paramas.mas.util


import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.TestKit
import org.paramas.mas.ActorUnitSpecs

class ReaperSpecs extends ActorUnitSpecs(ActorSystem("ReaperSpecs")) {

  "A Reaper actor" should {
      "terminate early when given an empty initial list of souls" in {
        fail
      }

      "terminate early when given an initial list of already terminated souls" in {

      }

      "ignore terminations from unknown souls" in {
             fail
      }

      "terminate when all souls are terminated" in {
        fail
      }
  }

  "A Promise Reaper" should {
    "complete the promise when terminated" in {
      fail
    }
  }

//  "An Echo actor" must {
//
//    "send back messages unchanged" in {
//      val echo = system.actorOf(Props[EchoActor])
//      echo ! "hello world"
//      expectMsg("hello world")
//    }
//
//  }
}


