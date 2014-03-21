package com.krzywicki.mas.async

import akka.actor.{PoisonPill, ActorRef, Props, Actor}
import scala.collection.mutable.ArrayBuffer
import com.krzywicki.mas.LogicTypes._
import com.krzywicki.mas.RootEnvironment

object Arena {

  case class Join(agent: Agent)

  def props(capacity: Int, meeting: (Population) => Population) = Props(classOf[Arena], capacity, meeting)
}

class Arena(val capacity: Int, val meeting: (Population) => Population) extends Actor {

  import RootEnvironment._
  import Arena._
  import Individual._

  val actors = ArrayBuffer.empty[ActorRef]
  val agents = ArrayBuffer.empty[Agent]

  def receive = {
    case Join(agent) =>
      actors += sender
      agents += agent
      if (agents.size == capacity) {
        performMeeting
      }
  }

  def performMeeting {
    val newAgents = meeting(agents.toList)

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
