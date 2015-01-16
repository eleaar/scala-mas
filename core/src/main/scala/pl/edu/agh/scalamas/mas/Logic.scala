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
package org.scalamas.mas

import org.scalamas.mas.LogicTypes._

/**
 * The types in a MAS simulation
 */
object LogicTypes {

  trait Agent

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