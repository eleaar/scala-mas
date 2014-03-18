package com.krzywicki.emas

import com.krzywicki.config.AppConfig
import com.krzywicki.util.MAS._
import com.krzywicki.mas.Logic

/**
 * Created by Daniel on 18.03.14.
 */
class EmasLogicImpl(implicit config: AppConfig) extends Logic {
   def initialPopulation = createPopulation _
   def behaviourFunction = behaviour _
   def meetingsFunction = meetings _
 }
