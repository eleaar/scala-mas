package com.krzywicki.hybrid

import com.krzywicki.util.Config
import com.krzywicki.util.Genetic._
import com.krzywicki.util.MAS._
import akka.actor.ActorSystem
import scala.concurrent.duration._
import akka.actor.Props
import akka.agent.Agent
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import com.krzywicki.util.Logger


object HybridApp {

  def main(args: Array[String]) {
    val problemSize = args(0).toInt
    val islandsNumber = args(1).toInt
    val duration = FiniteDuration(args(2).toLong,args(3))
    
    implicit val config = new Config(problemSize)

    val system = ActorSystem("hybrid")
    
    val logger = new Logger
    val migrator = system.actorOf(HybridMigrator.props)
    val islands = List.fill(islandsNumber)(system.actorOf(HybridIsland.props))
    
    islands.foreach(_ ! HybridIsland.Start(migrator, logger))
    
    system.scheduler.schedule(1 second, 1 second)(logger.print)
    system.scheduler.scheduleOnce(duration)(system.shutdown)
  }
}