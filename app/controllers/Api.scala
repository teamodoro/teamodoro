package controllers

import java.util.UUID

import models.Participant
import play.api.libs.json.Json
import play.api.mvc._
import shared.Shared

object Api extends Controller {

  def sessionHash(session: String): String = {
    val sha256 = java.security.MessageDigest.getInstance("SHA-256")
    sha256.digest(session.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

  def participants() = Action {
    Ok(Json.toJson(Shared.greenhouse.participants))
  }

  def current() = Action { request =>

    val sessionName = "teamodoro-session"
    val sessionId = request.session.get(sessionName).getOrElse(UUID.randomUUID.toString)
    val hash = sessionHash(sessionId)

    Shared.replaceGreenhouse(Shared.greenhouse.addParticipant(Participant.withSession(hash)))
    Shared.replaceGreenhouse(Shared.greenhouse.markAliveSession(sessionHash(sessionId)).tick())
    Ok(Json.toJson(Shared.greenhouse)).withSession(sessionName -> sessionId)
  }
}
