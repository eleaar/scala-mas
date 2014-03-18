package com.krzywicki.concur

import com.krzywicki.util.MAS._
import akka.actor.{Props, ActorRef, Actor}
import com.krzywicki.stat.{MeetingsInterceptor, Statistics}
import MeetingsInterceptor._
import com.krzywicki.stat.Statistics._
import scala.concurrent.ExecutionContext.Implicits.global
import com.krzywicki.emas.EmasRoot
import com.krzywicki.config.AppConfig

object ConcurrentIsland {

  def props(stats: Statistics) = Props(classOf[ConcurrentIsland], stats)
}

class ConcurrentIsland(implicit val stats: Statistics) extends Actor {

  import EmasRoot._

  implicit val settings = AppConfig(context.system)

  val supportedBehaviours = List(Migration, Fight, Reproduction, Death)
  val arenas = arenasForBehaviours(supportedBehaviours, migration orElse monitored(meetings))

  val population = createPopulation
  stats.update(population.maxBy(_.fitness).fitness, 0L)
  population.foreach(agent => context.actorOf(Individual.props(agent, arenas)))

  def receive = {
    case Add(agent) =>
      context.actorOf(Individual.props(agent, arenas))
  }

  def migration: Meetings = {
    case (Migration, agents) =>
      context.parent ! Migrate(agents);
      List.empty
  }

  def arenasForBehaviours(behaviours: List[Behaviour], meetings: Meetings) =
    behaviours map {
      behaviour =>
        val capacity = settings.emas.concurrent.capacities(behaviour)
        val meeting = (agents: List[Agent]) => meetings((behaviour, agents))
        behaviour -> context.actorOf(Arena.props(capacity, meeting), behaviour.toString)
    } toMap

}
