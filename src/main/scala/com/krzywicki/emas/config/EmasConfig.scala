package com.krzywicki.emas.config

import com.typesafe.config.Config
import com.krzywicki.mas.LogicTypes.{Migration, Behaviour}
import com.krzywicki.emas.EmasLogic.{Death, Reproduction, Fight}

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


