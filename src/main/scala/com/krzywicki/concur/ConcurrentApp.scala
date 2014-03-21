package com.krzywicki.concur

import scala.concurrent.duration._
import com.krzywicki.emas.EmasApp
import com.krzywicki.mas.async.AsyncEnvironment

object ConcurrentApp extends EmasApp {

  def main(args: Array[String]) {
    run("concurrent", AsyncEnvironment.props, 10 seconds)
  }
}