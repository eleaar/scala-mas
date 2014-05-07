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

package org.paramas.emas

import scala.math._
import org.paramas.emas.config.AppConfig

object Genetic {

  type Feature = Double
  type Solution = Array[Feature]
  type Fitness = Double

  def createSolution(implicit config: AppConfig): Solution =
    Array.fill(config.genetic.problemSize)(-50 + random * 100)

  def evaluate(solution: Solution): Fitness =
    - solution.foldLeft(0.0)(
      (sum, x) =>
        sum + 10 + x * x - 10 * cos(2 * Pi * x))

  def transform(s: Solution)(implicit config: AppConfig) = mutateSolution(s)

  def transform(s1: Solution, s2: Solution)(implicit config: AppConfig) =
    mutateSolutions(recombineSolutions(s1, s2))

  def mutateSolution(s: Solution)(implicit config: AppConfig) =
    if (random < config.genetic.mutationChance)
      s.map(f => if (random < config.genetic.mutationRate) mutateFeature(f) else f)
    else
      s

  def mutateSolutions(s: (Solution, Solution))(implicit config: AppConfig) =
    (mutateSolution(s._1), mutateSolution(s._2))

  def mutateFeature(f: Feature)(implicit config: AppConfig): Feature = {
    val range = math.random match {
      case x if x < 0.2 => 5.0
      case x if x < 0.4 => 0.2
      case _ => 1.0
    }
    f + range * tan(Pi * (random - 0.5))
  }

  def recombineSolutions(s1: Solution, s2: Solution): (Solution, Solution) = {
    val (s3, s4) = s1.zip(s2).map(recombineFeatures).unzip
    (s3.toArray, s4.toArray)
  }

  def recombineFeatures(features: (Feature, Feature)): (Feature, Feature) = {
    val a = min(features._1, features._2)
    val b = max(features._1, features._2)
    (a + (b - a) * random, a + (b - a) * random)
  }
}