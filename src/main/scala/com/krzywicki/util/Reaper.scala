package com.krzywicki.util

import akka.actor.{Terminated, Actor, Props, ActorRef}

/**
 * Created by krzywick on 03.03.14.
 */
object Reaper {
  def props(initialSouls: Seq[ActorRef]) = Props(classOf[Reaper], initialSouls)
}

class Reaper(initialSouls: Seq[ActorRef]) extends Actor {

  initialSouls foreach (context watch _)
  var activeSouls = initialSouls.toSet

  def receive = {
    case Terminated(soul) =>
      activeSouls -= soul
      if (activeSouls.isEmpty) context.system.shutdown
  }

}
