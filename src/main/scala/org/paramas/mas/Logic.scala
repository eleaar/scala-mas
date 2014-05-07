package org.paramas.mas

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

  import LogicTypes._

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





