package com.krzywicki.mas

import akka.actor.{Props, Actor}
import com.krzywicki.util.MAS.Agent
import scala.util.Random
import com.krzywicki.config.AppConfig

object RootEnvironment {

  case class Migrate(agents: List[Agent])

  case class Add(agent: Agent)

  def props(islandProps: Props) = Props(classOf[RootEnvironment], islandProps)
}

class RootEnvironment(islandProps: Props) extends Actor {

  import RootEnvironment._

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
