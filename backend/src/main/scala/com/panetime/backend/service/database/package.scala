package com.panetime.backend.service

import java.util.UUID

import com.panetime.backend.service.repository.{StateRepository, UserRepository}
import com.panetime.model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

package object database {

  object UserRepository extends UserRepository {

    override def authorize(attempt: Credentials): Future[Option[Token]] = {
      val userQuery =
        Db.credentials
          .filter(_.login === attempt.login)
          .filter(_.password === attempt.password)

      def updateToken(token: Token): Future[Int] =
        Db.run(userQuery.update(
          (attempt.login, attempt.password, Option(token.value)))
        )

      Db.run(userQuery.exists.result)
        .flatMap {
          case true ⇒
            val token = Token(attempt.login, UUID.randomUUID().toString)
            updateToken(token).map(_ ⇒ Option(token))
          case _    ⇒ Future.successful(None)
        }
    }

    override def hasValidSession(token: Token): Future[Boolean] =
      Db.run(
        Db.credentials
          .filter(_.login === token.login)
          .filter(_.token === Option(token.value))
          .exists
          .result
      )
  }

  object StateRepository extends StateRepository {

    override def getState(token: Token): Future[GroupState] = {
      val databaseResult = Db.run(
        Db.credentials.joinLeft(Db.state).on(_.login === _.login).result
      )
      databaseResult
        .map(_
          .map({
            case ((l1, _, _), Some((_, date))) ⇒ l1 -> Set(SelectedDate(date))
            case ((l1, _, _), None)            ⇒ l1 -> Set.empty[SelectedDate]
          })
          .groupBy(_._1)
          .mapValues(_.map(_._2).fold(Set.empty)(_ ++ _))
        )
        .map(GroupState.apply)
    }

    override def updateState(stateUpdate: StateUpdate): Future[Unit] =
      if (stateUpdate.selected)
        Db.run(Db.state += (stateUpdate.token.login, stateUpdate.date.date))
          .map(_ ⇒ Unit)
      else
        Db.run(
          Db.state
            .filter(_.login === stateUpdate.token.login)
            .filter(_.date === stateUpdate.date.date)
            .delete
        ).map(_ ⇒ Unit)
  }

}
