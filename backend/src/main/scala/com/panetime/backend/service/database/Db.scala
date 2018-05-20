package com.panetime.backend.service.database

import com.panetime.model.Types.{Date, Login}
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object Db {

  private implicit val duration: Duration = 3 seconds

  class CredentialsTable(tag: Tag) extends Table[(Login, String, Option[String])](tag, "CREDENTIALS") {

    def login = column[Login]("LOGIN", O.PrimaryKey)

    def password = column[String]("PASSWORD")

    def token = column[Option[String]]("TOKEN")

    def * = (login, password, token)
  }

  class StateTable(tag: Tag) extends Table[(Login, Date)](tag, "STATE") {

    def login = column[Login]("LOGIN")

    def date = column[Date]("DATE")

    def idx = index("unique_date_selection", (login, date), unique = true)

    def * = (login, date)
  }

  val credentials = TableQuery[CredentialsTable]

  val state = TableQuery[StateTable]

  private val applicationTables = List(credentials, state)

  val db = Database.forConfig("fileDb")

  def init(): Unit = {
    val existing = db.run(MTable.getTables)
    val createTablesIfDoNotExist = existing.flatMap(v ⇒ {
      val names = v.map(mt ⇒ mt.name.name)
      val createIfNotExist = applicationTables.filter(table ⇒
        !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
    Await.result(createTablesIfDoNotExist, Duration.Inf)
  }

  def run[T, E <: Effect](action: DBIOAction[T, NoStream, E]): Future[T] =
    db.run(action)
}
