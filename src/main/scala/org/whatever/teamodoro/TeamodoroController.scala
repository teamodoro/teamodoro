package org.whatever.teamodoro

import java.awt.Color

import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.whatever.teamodoro.models._
import scalate.ScalateSupport

import scala.concurrent.duration.Duration

class TeamodoroController extends TeamodoroStack with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  var greenhouse: Greenhouse = Greenhouse(
    "test",
    GreenhouseOptions(1, 1, 15),
    List(),
    State.Paused,
    System.currentTimeMillis(),
    0
  )

  def replaceGreenhouse(newGreenhouse: Greenhouse) = greenhouse.synchronized {
    greenhouse = newGreenhouse
  }

  get("/") {
    contentType = formats("json")
    replaceGreenhouse(greenhouse.tick())
    greenhouse
  }

  get("/start") {
    replaceGreenhouse(greenhouse.start())
    redirect("/")
  }

  get("/join/:name") {
    replaceGreenhouse(greenhouse.addParticipant(Participant(0, params("name"))))
    redirect("/")
  }
}
