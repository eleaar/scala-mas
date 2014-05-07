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
