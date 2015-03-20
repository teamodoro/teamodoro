
/**
 * Created by nsa, 22/01/15 
 */

import models._
import org.specs2.mutable._

class GreenhouseSpec extends Specification {

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

  "Greenhouse object" should {

    "be created properly" in {
      greenhouse.name must be("test")
      val duration = 25.minutes
      greenhouse.options.running.duration must beEqualTo(duration.inSeconds)
      greenhouse.options.running.color must beEqualTo("white")
      greenhouse.state must be(GreenhouseState.Running)
      greenhouse.timesBeforeLongBreak must beEqualTo(4)
      greenhouse.participants should have size 0
    }

    "handle new participants" in {
      val withVasya = greenhouse.addParticipant(Participant.withNameAndSession(Some("Вася"), "session"))

      withVasya.participants should have size 1
      withVasya.participants.head.name should beEqualTo(Some("Вася"))
    }

    "not allow to add user twice" in {
      val gh = List(
        Participant.withSession("1"),
        Participant.withSession("2"),
        Participant.withSession("1")
      ).foldLeft(Greenhouse.withName("uniq-participants")) {
        (g, p) => g.addParticipant(p)
      }

      gh.participants should have size 2
    }

    "kick out inactive users" in {

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
      (updatedLastAccess > 0) should beEqualTo(true)
      gh.markAliveSession("1").kickIdleParticipants.participants should have size 1
    }

    "handle 'outdated' state" in {
      var gh = Greenhouse.withName("outdated")
      gh.isOutdated should beEqualTo(false)

      gh = gh.copy(currentTime = gh.options.running.duration - 1)
      gh.isOutdated should beEqualTo(false)

      gh = gh.copy(currentTime = gh.options.running.duration + 1)
      gh.isOutdated should beEqualTo(true)
    }

    "decrement timesBeforeLongBreak and change states just as planned" in {
      var gh = Greenhouse.withName("decrement")
      gh.timesBeforeLongBreak should beEqualTo(gh.options.longBreakEvery)

      /*
        Steps of decrement
       */
      val testSequence = List(
        3 -> GreenhouseState.ShortBreak,
        3 -> GreenhouseState.Running,
        2 -> GreenhouseState.ShortBreak,
        2 -> GreenhouseState.Running,
        1 -> GreenhouseState.ShortBreak,
        1 -> GreenhouseState.Running,
        0 -> GreenhouseState.LongBreak,
        4 -> GreenhouseState.Running
      )

      testSequence.foreach {
        case (expectation, state) =>
          gh = makeOutdated(gh).tick()
          gh.state should beEqualTo(state)
          gh.timesBeforeLongBreak should beEqualTo(expectation)
      }

      true should beTrue
    }

    "catch up timer correctly" in {
      val gh = Greenhouse.withName("catching")

      gh.state should beEqualTo(GreenhouseState.Running)
      gh.catchUp.state should beEqualTo(GreenhouseState.Running)

      makeOutdated(gh).catchUp.state should beEqualTo(GreenhouseState.ShortBreak)

      /** Tomato (25 mins) + Break (5 mins) => Running */
      gh.copy(
        startTime = System.currentTimeMillis() -
          (greenhouse.options.running.duration * 1000) -
          (greenhouse.options.shortBreak.duration * 1000)
      ).catchUp.state should beEqualTo(GreenhouseState.Running)

      /** Tomato (25 mins) + Break (5 mins) - 1 minute => Short Break */
      gh.copy(
        startTime = System.currentTimeMillis() -
          (greenhouse.options.running.duration * 1000) -
          (greenhouse.options.shortBreak.duration * 1000) +
          (1.minute).inMillis
      ).catchUp.state should beEqualTo(GreenhouseState.ShortBreak)

      /** Tomato + 10 mins => Running */
      gh.copy(
        startTime = System.currentTimeMillis() -
          (greenhouse.options.running.duration * 1000) -
          (10.minutes).inMillis
      ).catchUp.state should beEqualTo(GreenhouseState.Running)

      /** Tomato + Tomato => Running */
      gh.copy(
        startTime = System.currentTimeMillis() -
          (greenhouse.options.running.duration * 1000) * 2
      ).catchUp.state should beEqualTo(GreenhouseState.Running)

      /** Tomato * 4 + Short Break * 3 => Long Break */
      gh.copy(
        startTime = System.currentTimeMillis() -
          (greenhouse.options.running.duration * 1000) * 4 -
          (greenhouse.options.shortBreak.duration * 1000) * 3
      ).catchUp.state should beEqualTo(GreenhouseState.LongBreak)
    }
  }
}