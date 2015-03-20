package models

import play.api.libs.json._

/**
 * Created by nsa, 20/03/15 
 */

object GreenhouseState extends Enumeration {
  type GreenhouseState = Value
  val Paused = Value("paused")
  val Running = Value("running")
  val ShortBreak = Value("shortBreak")
  val LongBreak = Value("longBreak")

  implicit object writes extends Writes[GreenhouseState] {
    def writes(p: GreenhouseState) = Json.obj(
      "name" -> Json.toJson(p.toString)
    )
  }
}

