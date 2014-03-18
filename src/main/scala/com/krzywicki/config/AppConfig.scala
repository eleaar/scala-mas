package com.krzywicki.config

import akka.actor.{Extension, ExtendedActorSystem, ExtensionIdProvider, ExtensionId}
import com.typesafe.config.Config

class AppConfig(config: Config) extends Extension {
  val emas = new EmasConfig(config.getConfig("emas"))
  val genetic = new GeneticConfig(config.getConfig("genetic"))
}

object AppConfig extends ExtensionId[AppConfig] with ExtensionIdProvider {

  override def lookup = AppConfig

  override def createExtension(system: ExtendedActorSystem) =
    new AppConfig(system.settings.config)
}
