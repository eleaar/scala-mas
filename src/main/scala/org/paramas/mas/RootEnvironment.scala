package org.paramas.mas

import akka.actor.{ActorContext, Props, Actor}
import scala.util.Random
import org.paramas.emas.config.AppConfig
import org.paramas.mas.LogicTypes._

object RootEnvironment {

  case class Migrate(agents: Seq[Agent])

  case class Add(agent: Agent)

  def props(islandProps: Props) = Props(classOf[RootEnvironment], islandProps)

  def migration(implicit context: ActorContext): MeetingFunction = {
    case (Migration(cap), agents) =>
      //      agents grouped(cap) foreach { context.parent ! Migrate(_)}
      context.parent ! Migrate(agents);
      List.empty
  }
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
