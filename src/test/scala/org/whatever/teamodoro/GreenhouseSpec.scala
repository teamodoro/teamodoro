package org.whatever.teamodoro

/**
 * Created by nsa, 22/01/15 
 */

import org.scalatest._
import org.whatever.teamodoro.models._

import scala.concurrent.duration._

class GreenhouseSpec extends FlatSpec with Matchers {

  val greenhouse = Greenhouse.withName("test")

  "Greenhouse" should "be created properly" in {
    greenhouse.name should equal("test")
    greenhouse.options.running.duration should equal((25 minutes).toSeconds)
    greenhouse.options.running.color should equal("white")
    greenhouse.state should equal(State.Running)
    greenhouse.timesBeforeLongBreak should equal(4)
  }

  "Greenhouse" should "handle new participants" in {
    val withVasya = greenhouse.addParticipant(Participant(0, "Вася"))
    withVasya.participants should have size 1
    withVasya.participants.head.name should equal("Вася")
  }
}
