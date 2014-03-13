package com.krzywicki.concur

import akka.actor.{ActorRef, Props, Actor}
import com.krzywicki.util.MAS._
import scala.collection.mutable.ArrayBuffer
import com.krzywicki.util.Config

object Arena {

  case class Join(agent: Agent)

  def props(capacity: Int) = Props(classOf[Arena], capacity)
}

class Arena(val capacity: Int, val behaviour: Behaviour, val config: Config) extends Actor {

  import Arena._
  import Individual._
  import ConcurrentIsland._

  val actors = ArrayBuffer.empty[ActorRef]
  val agents = ArrayBuffer.empty[Agent]

  def receive = {
    case Join(agent: Agent) =>
      actors += sender
      agents += agent
      if (agents.size == capacity) {
        performMeeting
      }
  }

  def performMeeting {
//    val (existingAgents, newAgents) =  meetings((behaviour, agents.toList)).splitAt(actors.size)
//
//    existingAgents.zip(actors).foreach {
//      case (agent, actor) => actor ! UpdateState(agent)
//    }
//    newAgents foreach {
//      agent => context.parent ! SpawnIndividual(agent)
//    }

    // zakończyc tych pustych

    actors.clear()
    agents.clear()
  }
}
