/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.scalamas.mas.async

import akka.actor.{Actor, ActorRef, Props}
import org.scalamas.mas.LogicTypes._
import org.scalamas.mas.async.Arena._
import org.scalamas.mas.async.Individual._

object Individual {

  case class UpdateState(state: Agent)

  def props(state: Agent, switchingBehaviour: (Agent) => ActorRef) =
    Props(classOf[Individual], state, switchingBehaviour)
}


class Individual(var state: Agent, val switchingBehaviour: (Agent) => ActorRef) extends Actor {

  override def preStart = joinArena

  def receive = {
    case UpdateState(s) =>
      state = s
      joinArena
  }

  def joinArena = switchingBehaviour(state) ! Join(state)
}