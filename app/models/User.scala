package models

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

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

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def login = column[String]("login")

    def passwordHash = column[String]("password_hash")

    def lastAccessTime = column[Long]("last_access_time")

    def * = (id, login.?, passwordHash, lastAccessTime) <> ((User.apply _).tupled, User.unapply)
  }

}
