package com.krzywicki.concur

import com.krzywicki.util.MAS._
import akka.actor.{Actor, Props, ActorRef}

object Individual {

  case class UpdateState(state: Agent)

  def props(state: Agent, arenas: Map[Behaviour, ActorRef]) =
    Props(classOf[Individual], state, arenas)
}

class Individual(var state: Agent, var arenas: Map[Behaviour, ActorRef]) extends Actor {

  import Individual._
  import Arena._

  implicit val settings = ConcurrentConfig(context.system)

  override def preStart = joinArena

  def receive = {
    case UpdateState(s) =>
      state = s
      joinArena
  }

  def joinArena = arenas(behaviour(state)) ! Join(state)
}