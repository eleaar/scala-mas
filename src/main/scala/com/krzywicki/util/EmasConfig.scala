package com.krzywicki.util


import akka.actor.Extension
import com.typesafe.config.Config
import akka.actor.{ExtendedActorSystem, ExtensionIdProvider, ExtensionId}

class EmasConfig(config: Config) extends Extension {
  val islandsNumber = config.getInt("islandsNumber")
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

object EmasConfig extends ExtensionId[EmasConfig] with ExtensionIdProvider {

  override def lookup = EmasConfig

  override def createExtension(system: ExtendedActorSystem) =
    new EmasConfig(system.settings.config.getConfig("emas"))
}

