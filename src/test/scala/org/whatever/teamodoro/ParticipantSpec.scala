package org.whatever.teamodoro

import org.scalatest._
import org.whatever.teamodoro.models._

/**
 * Created by nsa, 22/01/15 
 */

class ParticipantSpec extends FlatSpec with Matchers {

  "Participant" should "should be named or anonymous" in {
      val part = Participant.withNameAndSession(Some("Вася"), "session-string")
      part.name.get should equal("Вася")
      part.isAnonymous should equal(false)

      val anon = Participant.withSession("session-string")
      anon.isAnonymous should equal(true)
  }

  it should "be possible to mark participant alive" in {
    val part = Participant.withSession("session").copy(lastAccess = 0)
    part.markAlive.lastAccess should be > part.lastAccess
  }
}

