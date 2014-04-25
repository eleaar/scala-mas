package org.paramas.mas.util

import scala.collection.generic.CanBuildFrom
import scala.util.Random

object Util {

  /**
   * Implicit wrapper for Random.shuffle
   */
  implicit class Shuffled[T, CC[X] <: TraversableOnce[X]](xs: CC[T]) {
    /**
     * Implicit call to Random.shuffle
     */
    def shuffled(implicit bf: CanBuildFrom[CC[T], T, CC[T]]) = Random.shuffle(xs)
  }

}