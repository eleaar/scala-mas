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

package org.paramas.mas.util

import akka.actor._
import scala.concurrent.{ExecutionContext, Promise}
import akka.actor.Terminated
import scala.util.Success
import scala.concurrent.duration.FiniteDuration

object Reaper {
  def actorsTerminate(actors: Seq[ActorRef])(implicit system: ActorSystem) = {
    val p = Promise[Unit]()
    val callback = () => p.complete(Success(()))
    system.actorOf(Props(classOf[Reaper], actors, callback))
    p.future
  }

  def terminateAfter(actor: ActorRef, duration: FiniteDuration)(implicit system: ActorSystem, executionContext: ExecutionContext) = {
    system.scheduler.scheduleOnce(duration, actor, PoisonPill)
    actorsTerminate(List(actor))
  }
}

class Reaper(souls: Seq[ActorRef], callback: () => Unit) extends Actor with ActorLogging {
  var activeSouls = souls.toSet
  activeSouls foreach (context watch _)
  checkSouls

  def receive = {
    case Terminated(soul) if activeSouls.contains(soul) =>
      activeSouls -= soul
      checkSouls
  }

  def checkSouls = if (activeSouls.isEmpty) {
    log info "no more active souls"
    callback()
    context stop self
  }
}
