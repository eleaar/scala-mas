package org.scalamas.emas

import org.scalamas.mas.AgentRuntimeComponent
import org.scalamas.mas.LogicTypes.Migration
import org.scalamas.mas.logic.BehaviourStrategy
import org.scalamas.mas.random.RandomGenerator

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
