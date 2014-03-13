package com.krzywicki.concur

import akka.actor.{PoisonPill, ActorRef, Props, Actor}
import com.krzywicki.util.MAS._
import scala.collection.mutable.ArrayBuffer

object Arena {

  case class Join(agent: Agent)

  def props(capacity: Int, meeting: (List[Agent]) => List[Agent]) = Props(classOf[Arena], capacity, meeting)
}

class Arena(capacity: Int, meeting: (List[Agent]) => List[Agent]) extends Actor {

  import Arena._
  import Individual._
  import ConcurrentIsland._

  implicit val settings = ConcurrentConfig(context.system)

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
    val newAgents =  meeting(agents.toList)

    actors.zip(newAgents) foreach {
      case (actor, agent) => actor ! UpdateState(agent)
    }
    actors.drop(newAgents.size) foreach {
      actor => actor ! PoisonPill
    }
    newAgents.drop(actors.size) foreach {
      agent => context.parent ! Add(agent)
    }

    actors.clear()
    agents.clear()
  }
}
