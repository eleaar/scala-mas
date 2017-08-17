/*
 * Copyright (c) 2013 Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pl.edu.agh.scalamas.mas.async

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import pl.edu.agh.scalamas.mas.LogicTypes.{Agent, Population}
import pl.edu.agh.scalamas.mas.RootEnvironment.Add
import pl.edu.agh.scalamas.mas.async.Arena.Join
import pl.edu.agh.scalamas.mas.async.Individual.UpdateState

import scala.collection.mutable.ArrayBuffer

object Arena {

  case class Join(agent: Agent)

  def props(capacity: Int, meeting: (Population) => Population) = Props(classOf[Arena], capacity, meeting)
}

class Arena(val capacity: Int, val meeting: (Population) => Population) extends Actor {

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