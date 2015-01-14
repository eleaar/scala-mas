package org.scalamas.stats

/**
 * Created by Daniel on 2015-01-13.
 */
trait StatsComponent {

  type StatsType

  def stats: Stats[StatsType]

  def formatter: (StatsType) => String
}


