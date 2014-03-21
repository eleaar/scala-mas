package com.krzywicki.emas.config

import com.typesafe.config.Config
import com.krzywicki.emas.EmasLogic._
import com.krzywicki.mas.LogicTypes._

class ConcurrentConfig(config: Config) {

  val capacities = Map[Behaviour, Int](
    Fight -> config.getInt("fightCapacity"),
    Reproduction -> config.getInt("reproductionCapacity"),
    Migration -> config.getInt("migrationCapacity"),
    Death -> config.getInt("deathCapacity")
  )
}


