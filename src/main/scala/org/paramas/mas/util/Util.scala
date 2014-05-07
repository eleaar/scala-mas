/**
 * Copyright (C) 2013 - 2014, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * This file is part of ParaphraseAGH/Scala.
 *
 * ParaphraseAGH/Scala is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ParaphraseAGH/Scala is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ParaphraseAGH/Scala.  If not, see <http://www.gnu.org/licenses/>.
*/

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