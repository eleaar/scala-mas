package org.paramas.mas.async

import akka.actor.{Actor, Props}
import org.paramas.mas.{RootEnvironment, Logic, Environment}
import org.paramas.mas.LogicTypes._
import org.paramas.mas.RootEnvironment.Add

object AsyncEnvironment {

  def props(logic: Logic) = Props(classOf[AsyncEnvironment], logic)
}

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
