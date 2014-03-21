package com.krzywicki.emas.config

import com.typesafe.config.Config

class GeneticConfig(config: Config) {
  val problemSize = config.getInt("problemSize")
  val mutationRate = config.getDouble("mutationRate")
  val mutationRange = config.getDouble("mutationRange")
  val mutationChance = config.getDouble("mutationChance")
  val recombinationChance = config.getDouble("recombinationChance")
}
