package org.scalamas.mas.logic

import org.scalamas.mas.LogicTypes._

/**
 * Created by Daniel on 2015-01-14.
 */
trait MeetingsStrategy {

  def meetingsStrategy: MeetingsProvider

  trait MeetingsProvider {

    def meetingsFunction: MeetingFunction

  }

}
