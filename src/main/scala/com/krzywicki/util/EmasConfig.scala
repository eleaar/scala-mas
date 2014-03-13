package com.krzywicki.util

import com.typesafe.config.Config

class EmasConfig(config: Config) {
  val problemSize = config.getInt("problemSize")

  val populationSize = config.getInt("populationSize")
  val initialEnergy = config.getInt("initialEnergy")
  val reproductionThreshold = config.getInt("reproductionThreshold")
  val reproductionTransfer = config.getInt("reproductionTransfer")
  val fightTransfer = config.getInt("fightTransfer")

  val mutationRate = config.getDouble("mutationRate")
  val mutationRange = config.getDouble("mutationRange")
  val mutationChance = config.getDouble("mutationChance")
  val recombinationChance = config.getDouble("recombinationChance")
  val migrationProbability = config.getDouble("migrationProbability")
}

