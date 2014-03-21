package org.paramas.mas.util

import akka.actor._
import scala.concurrent.{ExecutionContext, Promise}
import akka.actor.Terminated
import scala.util.Success
import scala.concurrent.duration.FiniteDuration

object Reaper {
  def actorsTerminate(actors: Seq[ActorRef])(implicit system: ActorSystem) = {
    val p = Promise[Unit]()
    system.actorOf(Props(classOf[PromiseReaper], p, actors), "reaper")
    p.future
  }

  def terminateAfter(actor: ActorRef, duration: FiniteDuration)(implicit system: ActorSystem, executionContext: ExecutionContext) = {
    system.scheduler.scheduleOnce(duration, actor, PoisonPill)
    actorsTerminate(List(actor))
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
