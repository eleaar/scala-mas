package com.krzywicki.mas.async

import akka.actor.{Actor, Props, ActorRef}
import com.krzywicki.emas.config.AppConfig
import com.krzywicki.mas.LogicTypes._

object Individual {

  case class UpdateState(state: Agent)

  def props(state: Agent, switchingBehaviour: (Agent) => ActorRef) =
    Props(classOf[Individual], state, switchingBehaviour)
}


class Individual(var state: Agent, val switchingBehaviour: (Agent) => ActorRef) extends Actor {

  import Individual._
  import Arena._

  implicit val settings = AppConfig(context.system)

  override def preStart = joinArena

  def receive = {
    case UpdateState(s) =>
      state = s
      joinArena
  }

  def joinArena = switchingBehaviour(state) ! Join(state)
}
