package com.krzywicki.concur

import com.krzywicki.util.MAS._
import akka.actor.{Props, ActorRef, Actor}
import com.krzywicki.util.{Migrator, Statistics}
import com.krzywicki.util.MeetingsInterceptor._

object ConcurrentIsland {

  def props(migrator: ActorRef, stats: Statistics) = Props(classOf[ConcurrentIsland], migrator, stats)
}

class ConcurrentIsland(val migrator: ActorRef, val stats: Statistics) extends Actor {

  import Migrator._

  implicit val settings = ConcurrentConfig(context.system)

  migrator ! RegisterIsland(self)

  val supportedBehaviours = List(Migration, Fight, Reproduction, Death)
  val arenas = arenasForBehaviours(supportedBehaviours, (migration orElse meetings).intercepted {
    case (Reproduction, agents) => stats.update(agents.maxBy(_.fitness).fitness, agents.size/2)
  })

  val population = createPopulation
  stats.update(population.maxBy(_.fitness).fitness, 0L)
  population.foreach(agent => context.actorOf(Individual.props(agent, arenas)))

  def receive = {
    case Add(agent) =>
      stats.update(agent.fitness, 0L)
      context.actorOf(Individual.props(agent, arenas))
  }

  def migration: Meetings = {
    case (Migration, agents) =>
      migrator ! MigrateAgents(agents);
      List.empty
  }




  def arenasForBehaviours(behaviours: List[Behaviour], meetings: Meetings) =
    behaviours map {
      behaviour =>
        val capacity = settings.capacities(behaviour)
        val meeting = (agents: List[Agent]) => meetings((behaviour, agents))
        behaviour -> context.actorOf(Arena.props(capacity, meeting), behaviour.toString)
    } toMap

}
