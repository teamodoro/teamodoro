package models

import play.api.libs.json._

import java.util.Date
import java.sql.{ Date => SqlDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import java.sql.Timestamp

/**
 * Created by nsa, 19/01/15 
 */

object User {
  implicit object UserWrites extends Writes[User] {
    def writes(p: User) = Json.obj(
      "name" -> Json.toJson(p.name),
      "session" -> Json.toJson(p.session)
    )
  }

  def withNameAndSession(name: Option[String], session: String) =
    User(-1, name, session, System.currentTimeMillis())

  def withSession(session: String): User =
    User(-1, None, session, System.currentTimeMillis())
}

case class User(id: Long, name: Option[String], session: String, lastAccess: Long) {
  def isAnonymous: Boolean = name.isEmpty
  def markAlive: User = this.copy(lastAccess = System.currentTimeMillis())
  def fromLastAccess: Long = System.currentTimeMillis() - lastAccess
}

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.NotNull)
  def login = column[String]("login", O.NotNull)
  def passwordHash = column[String]("password_hash", O.NotNull)
  def lastAccessTime = column[Long]("last_access_time")
  def * = (id, login.?, passwordHash, lastAccessTime) <> ((User.apply _).tupled, User.unapply _)
}


