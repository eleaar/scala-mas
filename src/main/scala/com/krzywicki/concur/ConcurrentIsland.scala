package com.krzywicki.concur

import com.krzywicki.util.MAS._
import akka.actor.Props
import com.krzywicki.stat.Statistics
import com.krzywicki.stat.Statistics._
import com.krzywicki.mas.Environment

object ConcurrentIsland {

  def props(stats: Statistics) = Props(classOf[ConcurrentIsland], stats)
}

class ConcurrentIsland(implicit val stats: Statistics) extends Environment {

  val supportedBehaviours = List(Migration, Fight, Reproduction, Death)
  val arenas = arenasForBehaviours(supportedBehaviours, migration orElse monitored(meetings))

  val population = createPopulation
  stats.update(population.maxBy(_.fitness).fitness, 0L)
  population.foreach(agent => context.actorOf(Individual.props(agent, arenas)))

  def addAgent(agent: Agent) = context.actorOf(Individual.props(agent, arenas))

  def arenasForBehaviours(behaviours: List[Behaviour], meetings: Meetings) =
    behaviours map {
      behaviour =>
        val capacity = settings.emas.concurrent.capacities(behaviour)
        val meeting = (agents: List[Agent]) => meetings((behaviour, agents))
        behaviour -> context.actorOf(Arena.props(capacity, meeting), behaviour.toString)
    } toMap

}
