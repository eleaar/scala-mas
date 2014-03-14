package com.krzywicki.stat

import com.krzywicki.util.MAS._
import com.krzywicki.stat.MeetingsInterceptor._

class MeetingsInterceptor(meetings: Meetings, interceptor: Interceptor) extends Meetings {

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

  implicit class InterceptedMeetings(meetings: Meetings) {
    def intercepted(interceptor: Interceptor) = new MeetingsInterceptor(meetings, interceptor)
  }

}
