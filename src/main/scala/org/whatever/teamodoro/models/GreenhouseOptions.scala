package org.whatever.teamodoro.models

import scala.concurrent.duration.Duration

/**
 * Created by nsa, 19/01/15 
 */

case class DurationOptions(duration: Long, color: String)

case class GreenhouseOptions(running: DurationOptions,
                             shortBreak: DurationOptions,
                             longBreak: DurationOptions);
