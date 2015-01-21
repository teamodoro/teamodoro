package org.whatever.teamodoro

import java.awt.Color

import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.whatever.teamodoro.models._
import scalate.ScalateSupport

import scala.concurrent.duration._

class TeamodoroController extends TeamodoroStack with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  var greenhouse: Greenhouse = Greenhouse(
    "test",
    GreenhouseOptions(
      DurationOptions((25 minutes).toSeconds, "white"),
      DurationOptions((5 minutes).toSeconds, "green"),
      DurationOptions((15 minutes).toSeconds, "yellow"),
      4
    ),
    State.Running,
    4,
    System.currentTimeMillis(),
    0
  )

  def replaceGreenhouse(newGreenhouse: Greenhouse) = greenhouse.synchronized {
    greenhouse = newGreenhouse
  }

  get("/api*") {
    redirect("/")
  }

  get("/api/current") {
    contentType = formats("json")
    replaceGreenhouse(greenhouse.tick())
    greenhouse
  }

  get("/api/current/participants") {
    contentType = formats("json")
    greenhouse.participants
  }

  get("/api/start") {
    replaceGreenhouse(greenhouse.start())
    redirect("/")
  }
}

