package com.krzywicki.mas.async

import akka.actor.Props
import com.krzywicki.mas.{Logic, Environment}
import com.krzywicki.mas.LogicTypes._

object AsyncEnvironment {

  def props(logic: Logic) = Props(classOf[AsyncEnvironment], logic)
}

class AsyncEnvironment(logic: Logic) extends Environment {

  import logic._

  val arenas = arenasForBehaviours(behaviours, migration orElse meetingsFunction)
  val switchingBehaviour = (agent: Agent) => arenas(behaviourFunction(agent))

  initialPopulation.foreach(addAgent)

  def addAgent(agent: Agent) = context.actorOf(Individual.props(agent, switchingBehaviour))

  def arenasForBehaviours(behaviours: Seq[Behaviour], meetings: MeetingFunction) =
    behaviours map {
      behaviour =>
        val capacity = settings.emas.concurrent.capacities(behaviour)
        val meeting = (agents: List[Agent]) => meetings((behaviour, agents))
        behaviour -> context.actorOf(Arena.props(capacity, meeting), behaviour.toString)
    } toMap
}
