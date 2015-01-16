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

import akka.actor.{Actor, ActorContext, Props}
import org.scalamas.mas.LogicTypes._
import org.scalamas.mas.RootEnvironment._

import scala.util.Random

object RootEnvironment {

  /**
   * Message send from children islands to the root environment to request migration of the provided agents.
   * @param agents the agents to be migrated
   */
  case class Migrate(agents: Seq[Agent])

  /**
   * Message send from the root environment to its children islands to request the addition of an agent to the island.
   * @param agent the agent to be added to the island
   */
  case class Add(agent: Agent)

  /**
   * Props for a RootEnvironment.
   * @param islandProps the props of the island children
   * @param islandsNumber the number of children to spawn
   * @return the props for a RootEnvironment
   */
  def props(islandProps: Props, islandsNumber: Int) = Props(classOf[RootEnvironment], islandProps, islandsNumber)

  /**
   * Generic migration behaviour, which delegates the migration to the parent of the current context.
   * This parent is assumed to be a RootEnvironment
   * @param context current context
   * @return the result of a migration meeting - an empty list
   */
  def migration(implicit context: ActorContext): MeetingFunction = {
    case (Migration(cap), agents) =>
      //      agents grouped(cap) foreach { context.parent ! Migrate(_)}
      context.parent ! Migrate(agents);
      List.empty
  }
}

/**
 * Root environment for the simulation. Handles migrations between children islands.
 * @param islandProps the props of the island children
 * @param islandsNumber the number of children to spawn
 */
class RootEnvironment(islandProps: Props, islandsNumber: Int) extends Actor {
  require(islandsNumber > 0)

  val islands = Array.tabulate(islandsNumber)(i => context.actorOf(islandProps, s"island-$i"))

  def receive = {
    case Migrate(agents) =>
      agents.foreach {
        agent => randomIsland ! Add(agent)
      }
  }

  def randomIsland = islands(Random.nextInt(islands.size))
}