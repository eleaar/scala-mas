package org.scalamas.mas.random

import java.util.concurrent.ThreadLocalRandom

/**
 * Created by Daniel on 2014-09-22.
 */
trait RandomGenerator {
  def random: Double
}

trait DefaultRandomGenerator extends RandomGenerator {
  def random = math.random
}

trait ConcurrentRandomGenerator extends RandomGenerator {
  def random = ThreadLocalRandom.current().nextDouble()
}