package org.paramas.mas.util

import akka.actor._
import scala.concurrent.{ExecutionContext, Promise}
import akka.actor.Terminated
import scala.util.Success
import scala.concurrent.duration.FiniteDuration

object Reaper {
  def actorsTerminate(actors: Seq[ActorRef])(implicit system: ActorSystem) = {
    val p = Promise[Unit]()
    val callback = () => p.complete(Success())
    system.actorOf(Props(classOf[Reaper], actors, callback), "reaper")
    p.future
  }

  def terminateAfter(actor: ActorRef, duration: FiniteDuration)(implicit system: ActorSystem, executionContext: ExecutionContext) = {
    system.scheduler.scheduleOnce(duration, actor, PoisonPill)
    actorsTerminate(List(actor))
  }
}

class Reaper(souls: Seq[ActorRef], callback: () => Unit) extends Actor with ActorLogging {
  var activeSouls = souls.toSet
  activeSouls foreach (context watch _)
  checkSouls

  def receive = {
    case Terminated(soul) if activeSouls.contains(soul) =>
      activeSouls -= soul
      checkSouls
  }

  def checkSouls = if (activeSouls.isEmpty) {
    log info "no more active souls"
    callback()
    context stop self
  }
}
