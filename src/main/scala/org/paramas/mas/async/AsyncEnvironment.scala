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

import akka.actor.{Actor, Props}
import org.paramas.mas.{RootEnvironment, Logic, Environment}
import org.paramas.mas.LogicTypes._
import org.paramas.mas.RootEnvironment.Add

object AsyncEnvironment {

  def props(logic: Logic) = Props(classOf[AsyncEnvironment], logic)
}

/**
 * An asynchronous island implementation. This island spawns separate actors for all agents (individuals actors).
 * It also spawns a separate actor for every supported behaviour (arena actors). The individuals choose an arena based
 * on the behaviour function and the state of the agent and send the agent to the arena. Arenas gather incoming agents
 * and perform the meeting according to the meetings function and the capacity of the behaviour.
 *
 * @param logic the callbacks of the simulation
 */
class AsyncEnvironment(logic: Logic) extends Actor {
  import RootEnvironment._
  import logic._

  val arenas = arenasForBehaviours(behaviours, migration orElse meetingsFunction)
  val switchingBehaviour = (agent: Agent) => arenas(behaviourFunction(agent))

  initialPopulation.foreach(addAgent)

  def receive = {
    case Add(a) => addAgent(a)
  }

  def addAgent(agent: Agent) = context.actorOf(Individual.props(agent, switchingBehaviour))

  def arenasForBehaviours(behaviours: Seq[Behaviour], meetings: MeetingFunction) =
    behaviours map {
      behaviour =>
        val meeting = (agents: List[Agent]) => meetings((behaviour, agents))
        behaviour -> context.actorOf(Arena.props(behaviour.capacity, meeting), behaviour.getClass.getSimpleName)
    } toMap
}
