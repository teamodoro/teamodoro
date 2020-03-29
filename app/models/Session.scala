package models

import javax.inject.Inject
import play.api.db.slick._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

case class Session(id: Long, userId: Long, key: String)

class SessionDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class SessionTable(tag: Tag) extends Table[models.Session](tag, "SESSIONS") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def key = column[String]("key")

    def * = (id, userId, key) <> (Session.tupled, Session.unapply)
  }

}
