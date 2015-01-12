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

package org.scalamas.mas

/**
 * The types in a MAS simulation
 */
object LogicTypes {

  trait Agent {}

  /**
   * A collection of agents
   */
  type Population = List[Agent]

  /**
   * Possible agent behaviours
   */
  trait Behaviour {
    /**
     * How many agents with this behaviour should be grouped together.
     */
    def capacity: Int
  }

  /**
   * Predefined migration behaviour
   */
  case class Migration(capacity: Int) extends Behaviour

  /**
   * The behaviour function decides of the behaviour of an agent given its current state.
   */
  type BehaviourFunction = PartialFunction[Agent, Behaviour]

  /**
   * A group of agents with similar behaviour
   */
  type Group = (Behaviour, Population)

  /**
   * The meetings function transforms groups of similar agents into new subpopulations.
   */
  type MeetingFunction = PartialFunction[Group, Population]
}

/**
 * Actual logic of the simulation, in the form of several callback functions.
 */
trait Logic {

  import org.scalamas.mas.LogicTypes._

  /**
   * A generator fo the initial agent population on an island
   * @return an initial population of agents
   */
  def initialPopulation: Population

  /**
   * The behaviours to be supported for the agents in this simulation.
   * @return the behaviours to be supported for the agents in this simulation.
   */
  def behaviours: Seq[Behaviour]

  /**
   * Callback for the behaviour function. The behaviour function decides of the behaviour of an agent given its current state.
   * @return the agents behaviour function
   */
  def behaviourFunction: BehaviourFunction

  /**
   * Callback for the meeting function. The meetings function transforms groups of similar agents into new subpopulations.
   * @return the meeting function
   */
  def meetingsFunction: MeetingFunction
}





