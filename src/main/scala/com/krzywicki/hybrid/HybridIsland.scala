package com.krzywicki.hybrid

import akka.actor.Actor
import com.krzywicki.util.Config
import com.krzywicki.util.Genetic._
import com.krzywicki.util.MAS._
import com.krzywicki.util.MAS.Agent
import akka.actor.ActorLogging
import akka.actor.Props

object HybridIsland {
  case object Loop
  case class Migrate(agent: Agent)

  def props(implicit config: Config) = Props(classOf[HybridIsland], config)
}

class HybridIsland(implicit config: Config) extends Actor with ActorLogging {
  import HybridIsland._
  
  var population = createPopulation

  def receive = {
    case Loop =>
      population = population.groupBy(behaviour).flatMap(meetings).toList
      log.info(population.maxBy(_.fitness).fitness.toString)
      self ! Loop

    case Migrate(a) => population :+= a
  }

}