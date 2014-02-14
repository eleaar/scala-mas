package com.krzywicki.seq

import com.krzywicki.util.Config
import com.krzywicki.util.Genetic._
import com.krzywicki.util.Agent._

object SeqApp {

  def main(args: Array[String]) {

    implicit val config = new Config(100)

    var population = createPopulation
    for (i <- 1 to 1000) {
      population = population.groupBy(behaviour).flatMap {
        case (Death, _) => List()
        case (Fight, agents) => agents
//          agents.grouped(2).flatMap {
//            case List(a1,a2) => 
//          }
        case (Reproduction, agents) => agents
      }.toList
      
      println(population.maxBy(_._2)._2)
    }
  }
}