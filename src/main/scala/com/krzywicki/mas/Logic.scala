package com.krzywicki.mas

object LogicTypes {
  trait Agent {}
  type Population = List[Agent]

  trait Behaviour
  case object Migration extends Behaviour
  type BehaviourFunction = PartialFunction[Agent, Behaviour]

  type Group = (Behaviour, Population)
  type MeetingFunction = PartialFunction[Group, Population]
}

trait Logic {
  import LogicTypes._
  def initialPopulation: Population
  def behaviours: Seq[Behaviour]
  def behaviourFunction: BehaviourFunction
  def meetingsFunction: MeetingFunction
}





