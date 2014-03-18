package com.krzywicki.emas

import akka.actor.{Props, Actor}
import com.krzywicki.util.MAS.Agent
import com.krzywicki.util._
import scala.util.Random
import com.krzywicki.config.AppConfig

object EmasRoot {

  case class Migrate(agents: List[Agent])

  case class Add(agent: Agent)

  def props(islandProps: Props) = Props(classOf[EmasRoot], islandProps)
}

class EmasRoot(islandProps: Props) extends Actor {

  import EmasRoot._

  val settings = AppConfig(context.system)

  val islands = List.tabulate(settings.emas.islandsNumber)(i => context.actorOf(islandProps, s"island-$i"))

  def receive = {
    case Migrate(agents) =>
      agents.foreach {
        agent => randomFrom(islands) ! Add(agent)
      }
  }

  def randomFrom[T](list: List[T]) = list(Random.nextInt(list.size))
}
