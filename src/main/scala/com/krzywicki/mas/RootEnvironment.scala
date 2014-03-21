package com.krzywicki.mas

import akka.actor.{Props, Actor}
import scala.util.Random
import com.krzywicki.emas.config.AppConfig
import com.krzywicki.mas.LogicTypes._

object RootEnvironment {

  case class Migrate(agents: Seq[Agent])

  case class Add(agent: Agent)

  def props(islandProps: Props) = Props(classOf[RootEnvironment], islandProps)
}

class RootEnvironment(islandProps: Props) extends Actor {

  import RootEnvironment._

  val settings = AppConfig(context.system)
  val islands = Array.tabulate(settings.emas.islandsNumber)(i => context.actorOf(islandProps, s"island-$i"))

  def receive = {
    case Migrate(agents) =>
      agents.foreach {
        agent => randomIsland ! Add(agent)
      }
  }

  def randomIsland = islands(Random.nextInt(islands.size))
}
