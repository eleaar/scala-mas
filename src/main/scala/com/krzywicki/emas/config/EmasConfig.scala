package com.krzywicki.emas.config

import com.typesafe.config.Config

class EmasConfig(config: Config) {
  val islandsNumber = config.getInt("islandsNumber")
  val populationSize = config.getInt("populationSize")
  val initialEnergy = config.getInt("initialEnergy")
  val reproductionThreshold = config.getInt("reproductionThreshold")
  val reproductionTransfer = config.getInt("reproductionTransfer")
  val fightTransfer = config.getInt("fightTransfer")
  val migrationProbability = config.getDouble("migrationProbability")

  val concurrent = new ConcurrentConfig(config.getConfig("concurrent"))
}


