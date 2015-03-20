package models

import play.api.libs.json._

/**
 * Created by nsa, 19/01/15 
 */

object Participant {

  implicit object ParticipantWrites extends Writes[Participant] {
    def writes(p: Participant) = Json.obj(
      "name" -> Json.toJson(p.name),
      "session" -> Json.toJson(p.session)
    )
  }

  def withNameAndSession(name: Option[String], session: String) =
    Participant(name, session, System.currentTimeMillis())

  def withSession(session: String): Participant =
    Participant(None, session, System.currentTimeMillis())
}

case class Participant(name: Option[String], session: String, lastAccess: Long) {
  def isAnonymous: Boolean = name.isEmpty
  def markAlive: Participant = this.copy(lastAccess = System.currentTimeMillis())
  def fromLastAccess: Long = System.currentTimeMillis() - lastAccess
}
