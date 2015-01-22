package org.whatever.teamodoro.models

import scala.concurrent.duration._

/**
 * Created by nsa, 19/01/15 
 */

case class DurationOptions(duration: Long, color: String)

object Options {
  def default = Options(
    DurationOptions((25 minutes).toSeconds, "white"),
    DurationOptions((5 minutes).toSeconds, "green"),
    DurationOptions((15 minutes).toSeconds, "yellow"),
    4
  )
}

case class Options(running: DurationOptions,
                   shortBreak: DurationOptions,
                   longBreak: DurationOptions,
                   longBreakEvery: Int,
                   aliveTimeout: Int = 60);
