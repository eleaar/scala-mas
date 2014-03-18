package com.krzywicki.hybrid

import scala.concurrent.duration._
import com.krzywicki.emas.EmasApp

object HybridApp extends EmasApp {

  def main(args: Array[String]) {
    run("hybrid", HybridIsland.props, 10 seconds)
  }


}