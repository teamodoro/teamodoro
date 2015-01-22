package org.whatever.teamodoro

import java.awt.Color

import org.scalatra._
import org.json4s.{FieldSerializer, DefaultFormats, Formats}
import org.scalatra.json._
import org.whatever.teamodoro.models._
import scalate.ScalateSupport

import scala.concurrent.duration._

class TeamodoroController extends TeamodoroStack with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats + GreenhouseSerializer.serializer

  var greenhouse: Greenhouse = Greenhouse.withName("test")

  def replaceGreenhouse(newGreenhouse: Greenhouse) = greenhouse.synchronized {
    greenhouse = newGreenhouse
  }

  get("/api*") {
    redirect("/")
  }

  get("/api/current") {
    contentType = formats("json")

    val sha256 = java.security.MessageDigest.getInstance("SHA-256")
    var sessionHash = sha256.digest(session.getId.getBytes("UTF-8")).map("%02x".format(_)).mkString

    if (!session.isNew) {
      replaceGreenhouse(greenhouse.addParticipant(Participant.withSession(sessionHash)))
    }
    replaceGreenhouse(greenhouse.markAliveSession(sessionHash).tick())
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

  get("/session") {
    session
  }
}

