package com.krzywicki.hybrid

import com.krzywicki.util.{Reaper, Config, Logger}
import com.krzywicki.util.Genetic._
import com.krzywicki.util.MAS._
import akka.actor.ActorSystem
import scala.concurrent.duration._
import akka.actor.Props
import akka.agent.Agent
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import scala.concurrent.{Await, Future}
import akka.pattern._


object HybridApp {

  def main(args: Array[String]) {

    val problemSize = if (args.length > 0) args(0).toInt else 100
    val islandsNumber = if (args.length > 1) args(1).toInt else 1
    val duration = if (args.length > 3) FiniteDuration(args(2).toLong, args(3)) else 10 seconds

    implicit val config = new Config(problemSize)

    val system = ActorSystem("hybrid")

    val migrator = system.actorOf(HybridMigrator.props)
    val islands = List.fill(islandsNumber)(system.actorOf(HybridIsland.props(migrator)))
    system.actorOf(Reaper.props(islands))

    migrator ! HybridMigrator.RegisterIslands(islands)
    islands.foreach(_ ! HybridIsland.Start)
    system.scheduler.scheduleOnce(duration)(islands.foreach(_ ! HybridIsland.Stop))
  }
}