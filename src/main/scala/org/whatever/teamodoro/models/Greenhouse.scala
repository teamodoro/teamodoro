package org.whatever.teamodoro.models

import scala.concurrent.duration._

/**
 * Created by nsa, 19/01/15 
 */


object State extends Enumeration {
  type State = Value
  val Paused = Value("Paused")
  val Running = Value("Running")
  val ShortBreak = Value("ShortBreak")
  val LongBreak = Value("LongBreak")
}

import org.whatever.teamodoro.models.State._

case class Greenhouse(name: String,
                      options: GreenhouseOptions,
                      participants: List[Participant],
                      state: State,
                      currentTime: Long) {

  var startTime: Long = System.currentTimeMillis()

  def tick(): Greenhouse = {

    val updated = incrementTimer()

    if (updated.currentTime >= stopAt()) {
      return Greenhouse(
        this.name,
        this.options,
        this.participants,
        nextState(),
        updated.currentTime - stopAt()
      )
    }

    this.state match {
      case State.Paused => this
      case _ => updated
    }
  }

  def stopAt(): Long = this.state match {
    case State.Running => (this.options.pomodoroDuration minutes) toSeconds
    case State.ShortBreak => (this.options.normalBreakDuration minutes) toSeconds
    case _ => Long.MaxValue
  }

  def nextState(): State = this.state match {
    case State.Running => State.ShortBreak
    case State.ShortBreak => State.Running
    case State.Paused => State.Running
    case _ => State.Paused
  }

  def incrementTimer(): Greenhouse = {
    val greenhouse: Greenhouse = Greenhouse(
      this.name,
      this.options,
      this.participants,
      this.state,
      ((System.currentTimeMillis() - startTime) milliseconds) toSeconds
    )
    greenhouse.startTime = startTime
    greenhouse
  }

  def start(): Greenhouse = Greenhouse(
    this.name,
    this.options,
    this.participants,
    State.Running,
    this.currentTime
  )

  def addParticipant(participant: Participant): Greenhouse = Greenhouse(
    this.name,
    this.options,
    participant :: this.participants,
    this.state,
    this.currentTime
  ).tick()
}


