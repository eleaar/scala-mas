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

package org.scalamas.examples

import org.scalamas.app.ConcurrentStack
import org.scalamas.emas.EmasLogic
import org.scalamas.genetic.RastriginProblem
import org.scalamas.mas._

import scala.concurrent.duration._

/**
 * Created by Daniel on 2015-01-12.
 */
object EmasApp extends ConcurrentStack("emas")
  with SynchronousEnvironment
  with EmasLogic
  with RastriginProblem {

  def main(args: Array[String]) {
    run(5 seconds)
  }

}

