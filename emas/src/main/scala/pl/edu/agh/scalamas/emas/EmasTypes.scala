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

package org.scalamas.emas

import org.scalamas.genetic.GeneticOps
import org.scalamas.mas.LogicTypes
import org.scalamas.mas.LogicTypes._

object EmasTypes {

  case class Agent[G <: GeneticOps[G]](val solution: G#Solution, val fitness: G#Evaluation, var energy: Int) extends LogicTypes.Agent

  case class Death(capacity: Int) extends Behaviour

  case class Fight(capacity: Int) extends Behaviour

  case class Reproduction(capacity: Int) extends Behaviour

  def checked[G <: GeneticOps[G]](pop: Population) = pop.collect { case a: EmasTypes.Agent[G] => a}
}






