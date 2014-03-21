package com.krzywicki.mas

object LogicTypes {
  trait Agent {}
  type Population = List[Agent]

  trait Behaviour  {
    def capacity: Int
  }
  case class Migration(capacity: Int) extends Behaviour
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





