package com.krzywicki.concur

import com.krzywicki.util.MAS._
import akka.actor.{ActorRef, Actor}
import com.krzywicki.util.Statistics
import com.krzywicki.hybrid.HybridMigrator

object ConcurrentIsland {

  case class Add(agent: Agent)

}

class ConcurrentIsland(val migrator: ActorRef, val stats: Statistics) extends Actor {

  import ConcurrentIsland._

  implicit val settings = ConcurrentConfig(context.system)

  val arenas = arenasForBehaviours(List(Migration, Fight, Reproduction, Death), migration orElse meetings)

  def receive = {
    case Add(agent) => context.actorOf(Individual.props(agent, arenas))
  }

  def migration: PartialFunction[(Behaviour, List[Agent]), List[Agent]] = {
    case (Migration, agents) =>
      migrator ! HybridMigrator.ReceiveEmigrants(agents);
      List.empty
  }

  def arenasForBehaviours(behaviours: List[Behaviour], meetings: PartialFunction[(Behaviour, List[Agent]), List[Agent]]) =
    behaviours map {
      behaviour =>
        val capacity = settings.capacities(behaviour)
        val meeting = (agents: List[Agent]) => meetings((behaviour, agents))
        behaviour -> context.actorOf(Arena.props(capacity, meeting), behaviour.toString)
    } toMap

}
