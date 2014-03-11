package com.krzywicki.concur

import com.krzywicki.util.MAS._
import akka.actor.{Actor, Props, ActorRef}
import com.krzywicki.util.Config

object Individual {

  case class UpdateState(state: Agent)

  case class UpdateArenas(arenas: Map[Behaviour, ActorRef])

  def props(state: Agent, arenas: Map[Behaviour, ActorRef])(implicit config: Config) =
    Props(classOf[Individual], state, arenas, config)
}

class Individual(var state: Agent, var arenas: Map[Behaviour, ActorRef],  implicit val config: Config) extends Actor {

  import Individual._

  joinArena

  def receive = {
    case UpdateState(s) =>
      state = s
      joinArena
    case UpdateArenas(a) =>
      arenas = a
      joinArena
  }

  def joinArena = arenas(behaviour(state)) ! "join"

}
