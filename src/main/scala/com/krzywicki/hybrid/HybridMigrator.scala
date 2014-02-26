package com.krzywicki.hybrid

import akka.actor.Actor
import akka.actor.Props
import com.krzywicki.util.Config
import akka.actor.ActorRef
import scala.util.Random
import com.krzywicki.util.MAS.Agent

object HybridMigrator {
  case class RegisterIsland(ref: ActorRef)
  case class RecieveEmigrants(agents: List[Agent])

  def props(implicit config: Config) = Props(classOf[HybridMigrator])
}

class HybridMigrator extends Actor {
  import HybridMigrator._
  import HybridIsland._
  
  var islands = Set.empty[ActorRef]
  
  def receive = {
    case RegisterIsland(island) => islands += island
    case RecieveEmigrants(agents) =>
      val destinations = islands.toList
      agents.foreach {
        agent => randomFrom(destinations) ! Migrate(agent)
      }
  }
  
  def  randomFrom[T](list: List[T]) = list(Random.nextInt(list.size))
}