package org.whatever.teamodoro.models

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

case class Greenhouse(name: String,
                      options: GreenhouseOptions,
                      participants: List[Participant],
                      state: State,
                      startTime: Long,
                      currentTime: Long) {

  def tick(): Greenhouse = {
    val updated = incrementTimer()
    this.state match {
      case State.Paused => this
      case _ if updated.isOutdated => updated.catchUp
      case _ => updated
    }
  }

  def isOutdated: Boolean = this.currentTime >= stopAt

  def catchUp: Greenhouse = {
    val overdue = this.currentTime - stopAt
    val next = this.copy(
      state = nextState(),
      startTime = System.currentTimeMillis() - (overdue seconds).toMillis,
      currentTime = overdue
    )

    if (next.isOutdated) {
      return next.catchUp
    }
    next
  }

  def stopAt: Long = this.state match {
    case State.Running => this.options.longBreak.duration
    case State.ShortBreak => this.options.shortBreak.duration
    case State.LongBreak => this.options.longBreak.duration
    case _ => Long.MaxValue
  }

  def nextState(): State = this.state match {
    case State.Running => State.ShortBreak
    case State.ShortBreak => State.Running
    case State.Paused => State.Running
    case State.LongBreak => State.Running
    case _ => State.Paused
  }

  def incrementTimer(): Greenhouse = this.copy(
    currentTime = ((System.currentTimeMillis() - startTime) milliseconds) toSeconds
  )

  def start(): Greenhouse = this.copy(
    state = State.Running,
    startTime = System.currentTimeMillis()
  )

  def addParticipant(participant: Participant): Greenhouse = this.copy(
    participants = participant :: this.participants
  ) tick()
}


