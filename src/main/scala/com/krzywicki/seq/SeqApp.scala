package com.krzywicki.seq

import com.krzywicki.util.MAS._

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import com.krzywicki.config.AppConfig


object SeqApp {

  def main(args: Array[String]) {
    implicit val config = new AppConfig(ConfigFactory.load().getConfig("emas"))
    val deadline = 10 seconds fromNow

    var population = createPopulation
    while (deadline.hasTimeLeft) {
      population = population.groupBy(behaviour).flatMap(meetings).toList
      println(population.maxBy(_.fitness).fitness)
    }
  }
}