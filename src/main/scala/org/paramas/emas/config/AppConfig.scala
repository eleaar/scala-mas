/**
 * Copyright (C) 2013 - 2014, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * This file is part of ParaphraseAGH/Scala.
 *
 * ParaphraseAGH/Scala is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ParaphraseAGH/Scala is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ParaphraseAGH/Scala.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.paramas.emas.config

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
