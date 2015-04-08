package models

import play.api.libs.json._
import scala.concurrent.duration._

/**
 * Created by nsa, 19/01/15 
 */


object DurationOptions {

  implicit object writes extends Writes[DurationOptions] {

    def writes(p: DurationOptions) = Json.obj(
      "duration" -> Json.toJson(p.duration),
      "color" -> Json.toJson(p.color)
    )
  }

}

case class DurationOptions(duration: Long, color: String)

object Options {

  implicit object writes extends Writes[Options] {

    def writes(p: Options) = Json.obj(
      "running" -> Json.toJson(p.running),
      "shortBreak" -> Json.toJson(p.shortBreak),
      "longBreak" -> Json.toJson(p.longBreak),
      "longBreakEvery" -> Json.toJson(p.longBreakEvery)
    )
  }

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
