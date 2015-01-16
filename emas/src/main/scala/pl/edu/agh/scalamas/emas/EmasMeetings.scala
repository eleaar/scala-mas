/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.scalamas.emas

import org.scalamas.emas.EmasTypes._
import org.scalamas.emas.fight.FightStrategy
import org.scalamas.emas.reproduction.ReproductionStrategy
import org.scalamas.genetic.GeneticProblem
import org.scalamas.mas.LogicTypes.Migration
import org.scalamas.mas.logic.MeetingsStrategy
import org.scalamas.util.Util._

/**
 * Created by Daniel on 2015-01-14.
 */
trait EmasMeetings extends MeetingsStrategy {
  this: GeneticProblem
    with EmasStats
    with FightStrategy
    with ReproductionStrategy =>

  def meetingsStrategy = DefaultEmasMeeting

  object DefaultEmasMeeting extends MeetingsProvider {
    implicit val ordering = genetic.ordering

    // TODO fix shuffle bottleneck
    def meetingsFunction = {
      case (Death(_), _) => List.empty[Agent[Genetic]]
      case (Fight(cap), agents) =>
        checked[Genetic](agents).shuffled.grouped(cap).flatMap(fightStrategy.apply).toList
      case (Reproduction(cap), agents) =>
        val newAgents = checked[Genetic](agents).shuffled.grouped(cap).flatMap(reproductionStrategy.apply).toList
        stats.update((newAgents.maxBy(_.fitness).fitness, agents.size))
        newAgents
      case (Migration(_), agents) => agents
    }
  }

}