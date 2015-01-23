package org.whatever.teamodoro.models

import org.json4s.{FieldSerializer, DefaultFormats, Formats}

import scala.concurrent.duration._

/**
 * Created by nsa, 19/01/15 
 */

object State extends Enumeration {
  type State = Value
  val Paused = Value("paused")
  val Running = Value("running")
  val ShortBreak = Value("shortBreak")
  val LongBreak = Value("longBreak")
}

import org.whatever.teamodoro.models.State._

object GreenhouseSerializer {
  val serializer = FieldSerializer[Greenhouse](
    FieldSerializer.ignore("participants") orElse FieldSerializer.ignore("startTime")
  )
}

object Greenhouse {
  def withName(name: String): Greenhouse =
    Greenhouse(
      name,
      Options.default,
      State.Running,
      List(),
      4,
      System.currentTimeMillis(),
      0
    )
}


case class Greenhouse(name: String,
                      options: Options,
                      state: State,
                      participants: List[Participant],
                      timesBeforeLongBreak: Int,
                      startTime: Long,
                      currentTime: Long) {

  def isOutdated: Boolean = this.currentTime >= stopAt

  def tick(): Greenhouse = {
    this.state match {
      case State.Paused => this
      case _ => this.catchUp.kickIdleParticipants
    }
  }

  def catchUp: Greenhouse = {
    val updated = this.updateCurrentTime()

    if (!updated.isOutdated) {
      return updated
    }

    val overdue = updated.currentTime - stopAt

    val tillLongBreak = updated.state match {
      case State.Running => updated.timesBeforeLongBreak - 1
      case _ if updated.timesBeforeLongBreak <= 0 => updated.options.longBreakEvery
      case _ => updated.timesBeforeLongBreak
    }

    val next = updated.copy(
      state = nextState(),
      timesBeforeLongBreak = tillLongBreak,
      startTime = System.currentTimeMillis() - (overdue seconds).toMillis,
      currentTime = overdue
    )

    if (next.isOutdated) {
      return next.catchUp
    }
    next
  }

  def stopAt: Long = this.state match {
    case State.Running => this.options.running.duration
    case State.ShortBreak => this.options.shortBreak.duration
    case State.LongBreak => this.options.longBreak.duration
    case _ => Long.MaxValue
  }

  def nextState(): State = this.state match {
    case State.Running if timesBeforeLongBreak <= 1 => State.LongBreak
    case State.Running => State.ShortBreak
    case State.ShortBreak => State.Running
    case State.Paused => State.Running
    case State.LongBreak => State.Running
    case _ => State.Paused
  }

  def updateCurrentTime(): Greenhouse = this.copy(
    currentTime = ((System.currentTimeMillis() - startTime) milliseconds) toSeconds
  )

  def start(): Greenhouse = this.copy(
    state = State.Running,
    startTime = System.currentTimeMillis()
  )

  def addParticipant(participant: Participant): Greenhouse = {
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

  def kickIdleParticipants: Greenhouse = this.copy(
    participants = participants.filter(p => (p.fromLastAccess millis).toSeconds < options.aliveTimeout)
  )
}


