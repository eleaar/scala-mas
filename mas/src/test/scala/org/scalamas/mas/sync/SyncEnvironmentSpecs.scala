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

package org.paramas.mas.sync

import akka.actor.ActorSystem
import org.paramas.mas.ActorUnitSpecs

class SyncEnvironmentSpecs extends ActorUnitSpecs(ActorSystem("EnvironmentSpecs"))  {

  "An Environment actor" should {

    "return an empty list from a migration meeting" in {
      // given

//      fail()
    }

     "forward migrating agents to parent" in {
//       fail()
     }

  }

}
