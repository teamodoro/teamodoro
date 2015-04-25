package models

import java.util.Date
import java.sql.{ Date => SqlDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import java.sql.Timestamp

case class Session(id: Long, userId: Long, key: String)

class Sessions(tag: Tag) extends Table[Session](tag, "SESSIONS") {
  def id = column[Long]("id", O.PrimaryKey, O.NotNull, O.AutoInc)
  def userId = column[Long]("user_id", O.NotNull)
  def key = column[String]("key", O.NotNull)  
  def * = (id, userId, key) <> (Session.tupled, Session.unapply _)
}
