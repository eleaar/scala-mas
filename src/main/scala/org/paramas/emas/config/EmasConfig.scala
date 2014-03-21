package org.paramas.emas.config

import com.typesafe.config.Config
import org.paramas.mas.LogicTypes.{Migration, Behaviour}
import org.paramas.emas.EmasLogic.{Death, Reproduction, Fight}

class EmasConfig(config: Config) {
  val islandsNumber = config.getInt("islandsNumber")
  val populationSize = config.getInt("populationSize")
  val initialEnergy = config.getInt("initialEnergy")
  val reproductionThreshold = config.getInt("reproductionThreshold")
  val reproductionTransfer = config.getInt("reproductionTransfer")
  val fightTransfer = config.getInt("fightTransfer")
  val migrationProbability = config.getDouble("migrationProbability")

  val fightCapacity = config.getInt("fightCapacity")
  val reproductionCapacity = config.getInt("reproductionCapacity")
  val migrationCapacity = config.getInt("migrationCapacity")
  val deathCapacity = config.getInt("deathCapacity")
}


