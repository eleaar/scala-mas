package com.krzywicki.concur

import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import com.typesafe.config.Config
import com.krzywicki.util.EmasConfig
import com.krzywicki.util.MAS._

class ConcurrentConfig(config: Config) extends EmasConfig(config) {
  private val c = config.getConfig("concurrent")

  val capacities = Map[Behaviour, Int](
    Fight -> c.getInt("fightCapacity"),
    Reproduction -> c.getInt("reproductionCapacity"),
    Migration -> c.getInt("migrationCapacity"),
    Death -> c.getInt("deathCapacity")
  )
}

object ConcurrentConfig extends ExtensionId[ConcurrentConfig] with ExtensionIdProvider {

  override def lookup = ConcurrentConfig

  override def createExtension(system: ExtendedActorSystem) =
    new ConcurrentConfig(system.settings.config.getConfig("emas"))
}
