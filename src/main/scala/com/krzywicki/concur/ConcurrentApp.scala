package com.krzywicki.concur

import scala.concurrent.duration._
import com.krzywicki.emas.EmasApp

object ConcurrentApp extends EmasApp {

  def main(args: Array[String]) {
    run("concurrent", ConcurrentIsland.props, 10 seconds)
  }
}