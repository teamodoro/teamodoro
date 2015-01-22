package org.whatever.teamodoro

/**
 * Created by nsa, 22/01/15 
 */

import org.scalatest._
import org.whatever.teamodoro.models._

import scala.concurrent.duration._

class GreenhouseSpec extends FlatSpec with Matchers {

  val greenhouse = Greenhouse.withName("test")

  def makeOutdated(greenhouse: Greenhouse): Greenhouse = {
    greenhouse.copy(
      /**
       * Here is a trick
       *
       * Since tick() function relies on current time, to create outdated greenhouse
       * we should alter startTime, not currentTime.
       */
      startTime = System.currentTimeMillis() - (greenhouse.options.running.duration * 1000)
    )
  }

  "Greenhouse object" should "be created properly" in {
    greenhouse.name should equal("test")
    greenhouse.options.running.duration should equal((25 minutes).toSeconds)
    greenhouse.options.running.color should equal("white")
    greenhouse.state should equal(State.Running)
    greenhouse.timesBeforeLongBreak should equal(4)
    greenhouse.participants should have size 0
  }

  it should "handle new participants" in {
    val withVasya = greenhouse.addParticipant(Participant(0, "Вася"))
    withVasya.participants should have size 1
    withVasya.participants.head.name should equal("Вася")
  }

  it should "handle 'outdated' state" in {
    var gh = Greenhouse.withName("outdated")
    gh.isOutdated should equal(false)

    gh = gh.copy(currentTime = gh.options.running.duration - 1)
    gh.isOutdated should equal(false)

    gh = gh.copy(currentTime = gh.options.running.duration + 1)
    gh.isOutdated should equal(true)
  }

  it should "decrement timesBeforeLongBreak and change states just as planned" in {
    var gh = Greenhouse.withName("decrement")
    gh.timesBeforeLongBreak should equal(gh.options.longBreakEvery)

    /*
      Steps of decrement
     */
    val testSequence = List(
      3 -> State.ShortBreak,
      3 -> State.Running,
      2 -> State.ShortBreak,
      2 -> State.Running,
      1 -> State.ShortBreak,
      1 -> State.Running,
      0 -> State.LongBreak,
      4 -> State.Running
    )

    testSequence.foreach {
      case (expectation, state) =>
        gh = makeOutdated(gh).tick()
        gh.state should equal(state)
        gh.timesBeforeLongBreak should equal(expectation)
    }
  }

  it should "catch up timer correctly" in {
    fail("Not implemented")
  }
}
