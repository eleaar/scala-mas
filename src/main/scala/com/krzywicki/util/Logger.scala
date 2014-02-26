package com.krzywicki.util

import scala.concurrent.ExecutionContext

import org.slf4j.LoggerFactory

import akka.agent.Agent

class Logger(implicit context: ExecutionContext) {
  val log = LoggerFactory.getLogger(this.getClass)

  val fitness = Agent(Double.MinValue)
  val reproduction = Agent(0L)

  def print {
    val fit = fitness.future
    val rep = reproduction.future

    for (x <- fit; y <- rep) {
      println(s"fitness $x")
      println(s"reproduction $y")
    }
  }
}