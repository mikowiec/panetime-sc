package com.panetime.backend.service.chain

import com.panetime.backend.service.chain.Types.{Json, JsonReply}
import com.panetime.model.UserOnline
import prickle.Unpickle

import scala.concurrent.Future

object UserOnlineProcess extends PartialFunction[Json, JsonReply] {

  override def apply(json: Json): JsonReply = Future.successful(json)

  override def isDefinedAt(json: Json): Boolean =
    Unpickle[UserOnline].fromString(json).isSuccess
}
