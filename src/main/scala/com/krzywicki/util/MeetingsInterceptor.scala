package com.krzywicki.util

import com.krzywicki.util.MAS._

import com.krzywicki.util.MeetingsInterceptor._

class MeetingsInterceptor(meetings: Meetings, interceptor: Interceptor) extends Meetings {

  def apply(group: Group) = {
    val result = meetings.apply(group)
    if (interceptor.isDefinedAt(group)) {
      interceptor.apply(group)
    }
    result
  }

  def isDefinedAt(group: Group) = meetings.isDefinedAt(group)
}

object MeetingsInterceptor {

  type Interceptor = PartialFunction[Group, Unit]

  implicit class InterceptedMeetings(meetings: Meetings) {
    def intercepted(interceptor: Interceptor) = new MeetingsInterceptor(meetings, interceptor)
  }

}
