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

package org.paramas.mas.async

import akka.actor.{PoisonPill, ActorRef, Props, Actor}
import scala.collection.mutable.ArrayBuffer
import org.paramas.mas.LogicTypes._
import org.paramas.mas.RootEnvironment

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
