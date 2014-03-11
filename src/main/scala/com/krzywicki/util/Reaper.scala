package com.krzywicki.util

import akka.actor._
import scala.concurrent.Promise
import akka.actor.Terminated
import scala.util.Success

object Reaper {
  def actorsTerminate(actors: Seq[ActorRef])(implicit system: ActorSystem) = {
    val p = Promise[Unit]()
    system.actorOf(Props(classOf[PromiseReaper], p, actors), "reaper")
    p.future
  }
}

class PromiseReaper(p: Promise[Unit], actors: Seq[ActorRef]) extends Reaper(actors) {
  def onNoActiveSouls = p.complete(Success())
}

abstract class Reaper(souls: Seq[ActorRef]) extends Actor with ActorLogging {
  var activeSouls = souls.toSet
  activeSouls foreach (context watch _)

  def receive = {
    case Terminated(soul) =>
      activeSouls -= soul
      if (activeSouls.isEmpty) {
        log info "no more active souls"
        onNoActiveSouls
      }
  }

  def onNoActiveSouls
}
