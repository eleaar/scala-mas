package com.krzywicki.util

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import scala.util.Random
import com.krzywicki.util.MAS.Agent

object Migrator {

  case class RegisterIsland(island: ActorRef)

  case class MigrateAgents(agents: List[Agent])

  case class Add(agent: Agent)

  def props = Props(classOf[Migrator])
}

class Migrator extends Actor {

  import Migrator._

  var islands = List.empty[ActorRef]

  def receive = {
    case RegisterIsland(island) => islands :+= island
    case MigrateAgents(agents) =>
      agents.foreach {
        agent => randomFrom(islands) ! Add(agent)
      }
  }

  def randomFrom[T](list: List[T]) = list(Random.nextInt(list.size))
}