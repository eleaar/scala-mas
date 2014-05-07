/**
 * Copyright (C) 2013 - 2014, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * This file is part of ParaphraseAGH/Scala.
 *
 * ParaphraseAGH/Scala is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ParaphraseAGH/Scala is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ParaphraseAGH/Scala.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.paramas.mas.async

import akka.actor.{Actor, Props, ActorRef}
import org.paramas.emas.config.AppConfig
import org.paramas.mas.LogicTypes._

object Individual {

  case class UpdateState(state: Agent)

  def props(state: Agent, switchingBehaviour: (Agent) => ActorRef) =
    Props(classOf[Individual], state, switchingBehaviour)
}


class Individual(var state: Agent, val switchingBehaviour: (Agent) => ActorRef) extends Actor {

  import Individual._
  import Arena._

  override def preStart = joinArena

  def receive = {
    case UpdateState(s) =>
      state = s
      joinArena
  }

  def joinArena = switchingBehaviour(state) ! Join(state)
}
