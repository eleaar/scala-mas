package com.krzywicki.seq

import com.krzywicki.util.Config
import com.krzywicki.util.Genetic._
import com.krzywicki.util.MAS._

import scala.concurrent.duration._

object SeqApp {

  def main(args: Array[String]) {
    implicit val config = new Config(100)
    val deadline = 10 seconds fromNow
    
    var population = createPopulation
    while(deadline.hasTimeLeft) {
      population = population.groupBy(behaviour).flatMap(meetings).toList
      println(population.maxBy(_.fitness).fitness)
    }
  }
}