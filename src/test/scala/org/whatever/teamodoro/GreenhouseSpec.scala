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
    val withVasya = greenhouse.addParticipant(Participant.withNameAndSession(Some("Вася"), "session"))

    withVasya.participants should have size 1
    withVasya.participants.head.name should equal(Some("Вася"))
  }

  it should "not allow to add user twice" in {
    val gh = List(
      Participant.withSession("1"),
      Participant.withSession("2"),
      Participant.withSession("1")
    ).foldLeft(Greenhouse.withName("uniq-participants")) {
      (g, p) => g.addParticipant(p)
    }

    gh.participants should have size 2
  }

  it should "kick out inactive users" in {

    val participants = List(
      Participant.withSession("1").copy(lastAccess = 0),
      Participant.withSession("2").copy(lastAccess = 1),
      Participant.withSession("3").copy(lastAccess = 2)
    )

    val gh = participants.foldLeft(Greenhouse.withName("kicking-greenhouse")) {
      (g, p) => g.addParticipant(p)
    }

    val updatedLastAccess = gh.markAliveSession("1").participants.find(_.session == "1") match {
      case Some(p) => p.lastAccess
      case _ => 0
    }
    (updatedLastAccess > 0) should equal(true)
    gh.markAliveSession("1").kickIdleParticipants.participants should have size 1
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
    val gh = Greenhouse.withName("catching")

    gh.state should equal(State.Running)
    gh.catchUp.state should equal(State.Running)

    makeOutdated(gh).catchUp.state should equal(State.ShortBreak)

    /** Tomato (25 mins) + Break (5 mins) => Running */
    gh.copy(
      startTime = System.currentTimeMillis() -
        (greenhouse.options.running.duration * 1000) -
        (greenhouse.options.shortBreak.duration * 1000)
    ).catchUp.state should equal(State.Running)

    /** Tomato (25 mins) + Break (5 mins) - 1 minute => Short Break */
    gh.copy(
      startTime = System.currentTimeMillis() -
        (greenhouse.options.running.duration * 1000) -
        (greenhouse.options.shortBreak.duration * 1000) +
        (1 minute).toMillis
    ).catchUp.state should equal(State.ShortBreak)

    /** Tomato + 10 mins => Running */
    gh.copy(
      startTime = System.currentTimeMillis() -
        (greenhouse.options.running.duration * 1000) -
        (10 minutes).toMillis
    ).catchUp.state should equal(State.Running)

    /** Tomato + Tomato => Running */
    gh.copy(
      startTime = System.currentTimeMillis() -
        (greenhouse.options.running.duration * 1000) * 2
    ).catchUp.state should equal(State.Running)

    /** Tomato * 4 + Short Break * 3 => Long Break */
    gh.copy(
      startTime = System.currentTimeMillis() -
        (greenhouse.options.running.duration * 1000) * 4 -
        (greenhouse.options.shortBreak.duration * 1000) * 3
    ).catchUp.state should equal(State.LongBreak)
  }
}
