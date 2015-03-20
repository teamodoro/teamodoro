package org.whatever.teamodoro

import models._
import org.specs2.mutable.Specification

/**
 * Created by nsa, 22/01/15 
 */

class ParticipantSpec extends Specification {

  "Participant" should {

    "should be named or anonymous" in {
      val part = Participant.withNameAndSession(Some("Вася"), "session-string")
      part.name.get must beEqualTo("Вася")
      part.isAnonymous must beFalse

      val anon = Participant.withSession("session-string")
      anon.isAnonymous must beTrue
    }

    "be possible to mark participant alive" in {
      val part = Participant.withSession("session").copy(lastAccess = 0)
      part.markAlive.lastAccess must be > part.lastAccess
    }
  }
}

