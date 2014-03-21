package com.krzywicki.seq

import com.krzywicki.emas.EmasLogic
import com.krzywicki.emas.EmasLogic._

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import com.krzywicki.emas.config.AppConfig

object SeqApp {

  def main(args: Array[String]) {
    implicit val config = new AppConfig(ConfigFactory.load().getConfig("emas"))

    val deadline = 10 seconds fromNow

    val logic = new EmasLogic
    import logic._

    var population = initialPopulation
    while (deadline.hasTimeLeft) {
      population = population.groupBy(behaviourFunction).flatMap(meetingsFunction).toList
      println(checked(population).maxBy(_.fitness).fitness)
    }
  }
}