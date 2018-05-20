package com.panetime.backend.service.chain

import com.panetime.backend.service.chain.Types.{Json, JsonReply}
import com.panetime.backend.service.repository.UserRepository
import com.panetime.model.Token
import prickle.Pickle.{intoString ⇒ write}
import prickle.Unpickle

import scala.concurrent.ExecutionContext

class TokenCheckProcess(userRepository: UserRepository)
                       (implicit ctx: ExecutionContext)
  extends PartialFunction[Json, JsonReply] {

  override def isDefinedAt(json: Json): Boolean =
    Unpickle[Token].fromString(json).isSuccess

  override def apply(json: Json): JsonReply = {
    val attempt = Unpickle[Token].fromString(json).get
    userRepository.hasValidSession(attempt)
      .map {
        case true ⇒ write(attempt)
        case _    ⇒ Types.noOp
      }
  }
}
