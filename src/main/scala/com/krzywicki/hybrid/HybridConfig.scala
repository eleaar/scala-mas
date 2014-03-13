package com.krzywicki.hybrid

import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import com.typesafe.config.Config
import com.krzywicki.util.EmasConfig

class HybridConfig(config: Config) extends EmasConfig(config) with Extension {

}

object HybridConfig extends ExtensionId[HybridConfig] with ExtensionIdProvider {

  override def lookup = HybridConfig

  override def createExtension(system: ExtendedActorSystem) =
    new HybridConfig(system.settings.config.getConfig("emas"))
}
