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

package org.paramas.mas.sync

import akka.actor._
import org.paramas.mas.{RootEnvironment, Logic}

object SyncEnvironment {

  case object Loop

  def props(logic: Logic) = Props(classOf[SyncEnvironment], logic)
}

/**
 * A synchronous island implementation. This actor spins in a messages loop. In each iteration it updates synchronously
 * the population accordingly to the behaviour and meetings functions. This islands supports migration, assuming its
 * parent is a RootEnvironment.
 *
 * @param logic the callbacks for the simulation
 */
class SyncEnvironment(logic: Logic) extends Actor {
  import RootEnvironment._
  import SyncEnvironment._
  import logic._

  var population = initialPopulation
  self ! Loop

  override def receive = {
    case Loop =>
      population = population.groupBy(behaviourFunction).flatMap(migration orElse meetingsFunction).toList
      self ! Loop
    case Add(agent) => population :+= agent
  }
}