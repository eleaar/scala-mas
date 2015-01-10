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

import com.typesafe.config.Config
import org.paramas.mas.LogicTypes.{Migration, Behaviour}
import org.paramas.emas.EmasLogic.{Death, Reproduction, Fight}

class EmasConfig(config: Config) {
  val islandsNumber = config.getInt("islandsNumber")
  val populationSize = config.getInt("populationSize")
  val initialEnergy = config.getInt("initialEnergy")
  val reproductionThreshold = config.getInt("reproductionThreshold")
  val reproductionTransfer = config.getInt("reproductionTransfer")
  val fightTransfer = config.getInt("fightTransfer")
  val migrationProbability = config.getDouble("migrationProbability")

  val fightCapacity = config.getInt("fightCapacity")
  val reproductionCapacity = config.getInt("reproductionCapacity")
  val migrationCapacity = config.getInt("migrationCapacity")
  val deathCapacity = config.getInt("deathCapacity")
}


