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

import org.scalamas.app.AgentRuntimeComponent
import org.scalamas.mas.LogicTypes.Migration
import org.scalamas.mas.logic.BehaviourStrategy
import org.scalamas.random.RandomGenerator

/**
 * Created by Daniel on 2015-01-14.
 */
trait EmasBehaviour extends BehaviourStrategy {
  this: AgentRuntimeComponent with RandomGenerator =>

  def behaviourStrategy = DefaultEmasBehaviour

  object DefaultEmasBehaviour extends BehaviourProvider {

    import org.scalamas.emas.EmasTypes._

    val config = agentRuntime.config.getConfig("emas")
    val fightCapacity = config.getInt("fightCapacity")
    val reproductionCapacity = config.getInt("reproductionCapacity")
    val migrationCapacity = config.getInt("migrationCapacity")
    val deathCapacity = config.getInt("deathCapacity")
    val migrationProbability = config.getDouble("migrationProbability")
    val reproductionThreshold = config.getInt("reproductionThreshold")

    val death = Death(deathCapacity)
    val fight = Fight(fightCapacity)
    val reproduce = Reproduction(reproductionCapacity)
    val migrate = Migration(migrationCapacity)

    val behaviours = List(death, fight, reproduce, migrate)

    def behaviourFunction = {
      case Agent(_, _, energy) => energy match {
        case 0 => death
        case _ if random < migrationProbability => migrate
        case energy if energy >= reproductionThreshold => reproduce
        case _ => fight
      }
    }
  }

}