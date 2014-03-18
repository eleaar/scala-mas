package com.krzywicki.concur

import com.krzywicki.util.MAS._
import akka.actor.{Actor, Props, ActorRef}
import com.krzywicki.config.{AppConfig, ConcurrentConfig}

object Individual {

  case class UpdateState(state: Agent)

  def props(state: Agent, arenas: Map[Behaviour, ActorRef]) =
    Props(classOf[Individual], state, arenas)
}

class Individual(var state: Agent, val arenas: Map[Behaviour, ActorRef]) extends Actor {

  import Individual._
  import Arena._

  implicit val settings = AppConfig(context.system)

  override def preStart = joinArena

  def receive = {
    case UpdateState(s) =>
      state = s
      joinArena
  }

  def joinArena = arenas(behaviour(state)) ! Join(state)
}
