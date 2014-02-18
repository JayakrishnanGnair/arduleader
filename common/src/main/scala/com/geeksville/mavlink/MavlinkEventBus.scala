package com.geeksville.mavlink

import org.mavlink.messages.MAVLinkMessage
import com.geeksville.akka.EventStream
import com.geeksville.logback.Logging
import akka.actor.Actor
import akka.actor.ActorRef

object MavlinkEventBus extends EventStream with Logging {

  /**
   * @param sysId use -1 for any system
   */
  def subscribe(a: ActorRef, sysId: Int) = {
    def interested(ev: Any) = {
      val r = ev match {
        case SendYoungest(m) if (m.sysId == sysId) || (sysId == -1) => true
        case m: MAVLinkMessage if (m.sysId == sysId) || (sysId == -1) => true
        case _ => false
      }

      //logger.debug("%s: interest in %s (myId=%d) is %s".format(a, ev, sysId, r))
      r
    }

    super.subscribe(a, interested _)
  }
}

