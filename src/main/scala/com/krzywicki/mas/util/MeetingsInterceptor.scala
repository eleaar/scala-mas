package com.krzywicki.mas.util

import com.krzywicki.mas.LogicTypes._
import com.krzywicki.mas.util.MeetingsInterceptor.Interceptor

class MeetingsInterceptor(meetings: MeetingFunction, interceptor: Interceptor) extends MeetingFunction {

  def apply(group: Group) = {
    val behaviour = group._1
    val agentsBefore = group._2
    val agentsAfter = meetings.apply(group)
    if (interceptor.isDefinedAt((behaviour, agentsBefore, agentsAfter))) {
      interceptor.apply((behaviour, agentsBefore, agentsAfter))
    }
    agentsAfter
  }

  def isDefinedAt(group: Group) = meetings.isDefinedAt(group)
}

object MeetingsInterceptor {

  type Interceptor = PartialFunction[(Behaviour, Population, Population), Unit]

  implicit class InterceptedMeetings(meetings: MeetingFunction) {
    def intercepted(interceptor: Interceptor) = new MeetingsInterceptor(meetings, interceptor)
  }

}
