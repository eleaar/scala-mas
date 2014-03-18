package com.krzywicki.config

import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import com.typesafe.config.Config
import com.krzywicki.util.MAS._

class ConcurrentConfig(config: Config) {

  val capacities = Map[Behaviour, Int](
    Fight -> config.getInt("fightCapacity"),
    Reproduction -> config.getInt("reproductionCapacity"),
    Migration -> config.getInt("migrationCapacity"),
    Death -> config.getInt("deathCapacity")
  )
}


