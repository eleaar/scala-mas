/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.edu.agh.scalamas.mas.async

import akka.actor.{Actor, Props}
import pl.edu.agh.scalamas.mas.Logic
import pl.edu.agh.scalamas.mas.LogicTypes._
import pl.edu.agh.scalamas.mas.RootEnvironment.{Add, migration}

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

  val arenas = arenasForBehaviours(logic.behaviours, migration orElse logic.meetingsFunction)
  val switchingBehaviour = (agent: Agent) => arenas(logic.behaviourFunction(agent))

  logic.initialPopulation.foreach(addAgent)

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