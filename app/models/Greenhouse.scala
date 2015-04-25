package models

import models.GreenhouseState.GreenhouseState
import scala.concurrent.duration._

/**
 * Created by nsa, 19/01/15 
 */

object Greenhouse {

  import play.api.libs.json._

  implicit object GreenhouseWrites extends Writes[Greenhouse] {
    def writes(p: Greenhouse) = Json.obj(
      "name"                 -> Json.toJson(p.name),
      "state"                -> Json.toJson(p.state),
      "options"              -> Json.toJson(p.options),
      "timesBeforeLongBreak" -> Json.toJson(p.timesBeforeLongBreak),
      "startTime"            -> Json.toJson(p.startTime),
      "participants"         -> Json.toJson(p.participants),
      "currentTime"          -> Json.toJson(p.currentTime)
    )
  }

  def withName(name: String): Greenhouse = {
    Greenhouse(
      name, 
      Options.default, 
      GreenhouseState.Running, 
      List(), 
      4, 
      System.currentTimeMillis(), 
      0)
  }
}

case class Greenhouse(
  name: String,
  options: Options,
  state: GreenhouseState,
  participants: List[User],
  timesBeforeLongBreak: Int,
  startTime: Long,
  currentTime: Long) {

    def isOutdated: Boolean = this.currentTime >= stopAt

    def tick(): Greenhouse = this.state match {
      case GreenhouseState.Paused => this
      case _ => this.catchUp.kickIdleUsers
    }

    def catchUp: Greenhouse = {
      val updated = this.updateCurrentTime()

      if (!updated.isOutdated) {
        return updated
      }

      val overdue = updated.currentTime - stopAt

      val tillLongBreak = updated.state match {
        case GreenhouseState.Running => updated.timesBeforeLongBreak - 1
        case _ if updated.timesBeforeLongBreak <= 0 => updated.options.longBreakEvery
        case _ => updated.timesBeforeLongBreak
      }

      val next = updated.copy(
        state = nextState(),
        timesBeforeLongBreak = tillLongBreak,
        startTime = System.currentTimeMillis() - overdue.seconds.toMillis,
        currentTime = overdue
      )

      if (next.isOutdated) {
        return next.catchUp
      }
      next
    }

    def stopAt: Long = this.state match {
      case GreenhouseState.Running => this.options.running.duration
      case GreenhouseState.ShortBreak => this.options.shortBreak.duration
      case GreenhouseState.LongBreak => this.options.longBreak.duration
      case _ => Long.MaxValue
    }

    def nextState(): GreenhouseState = this.state match {
      case GreenhouseState.Running if timesBeforeLongBreak <= 1 => GreenhouseState.LongBreak
      case GreenhouseState.Running => GreenhouseState.ShortBreak
      case GreenhouseState.ShortBreak => GreenhouseState.Running
      case GreenhouseState.Paused => GreenhouseState.Running
      case GreenhouseState.LongBreak => GreenhouseState.Running
      case _ => GreenhouseState.Paused
    }

    def updateCurrentTime(): Greenhouse = this.copy(
      currentTime = (System.currentTimeMillis() - startTime).milliseconds.toSeconds
    )

  def start(): Greenhouse = this.copy(
    state = GreenhouseState.Running,
    startTime = System.currentTimeMillis())

  def addUser(participant: User): Greenhouse = { 
    participants.find(_.session == participant.session) match {
      case Some(_) => this
      case None => this.copy(
        participants = participant :: participants
      )
    }
  }

  def markAliveSession(session: String): Greenhouse = this.copy(
    participants = participants.map {
      p => if (p.session == session) p.markAlive else p
    }
  )

  def kickIdleUsers: Greenhouse = this.copy(
    participants = participants.filter(p => 
        p.fromLastAccess.millis.toSeconds < options.aliveTimeout
      )
    )
  }

