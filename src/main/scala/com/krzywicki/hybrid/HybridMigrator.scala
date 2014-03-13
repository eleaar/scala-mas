package com.krzywicki.hybrid

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import scala.util.Random
import com.krzywicki.util.MAS.Agent
import com.krzywicki.concur.ConcurrentConfig

object HybridMigrator {

  case class RegisterIsland(island: ActorRef)

  case class ReceiveEmigrants(agents: List[Agent])

  def props = Props(classOf[HybridMigrator])
}

class HybridMigrator extends Actor {

  import HybridMigrator._
  import HybridIsland._

  implicit val settings = ConcurrentConfig(context.system)

  var islands = List.empty[ActorRef]

  def receive = {
    case RegisterIsland(island) => islands :+= island
    case ReceiveEmigrants(agents) =>
      agents.foreach {
        agent => randomFrom(islands) ! Migrate(agent)
      }
  }

  def randomFrom[T](list: List[T]) = list(Random.nextInt(list.size))
}