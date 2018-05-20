package com.panetime.backend.service.chain

import com.panetime.backend.service.chain.Types.{Json, JsonReply}
import com.panetime.backend.service.repository.{StateRepository, UserRepository}
import com.panetime.model.StateUpdate
import prickle.Pickle.{intoString ⇒ write}
import prickle.Unpickle

import scala.concurrent.{ExecutionContext, Future}

class StateUpdateProcess(stateRepository: StateRepository, userRepository: UserRepository)
                        (implicit ctx: ExecutionContext)
  extends PartialFunction[Json, JsonReply] {

  override def apply(json: Json): JsonReply = {
    val stateUpdate = Unpickle[StateUpdate].fromString(json).get
    userRepository
      .hasValidSession(stateUpdate.token)
      .flatMap {
        case true ⇒
          stateRepository
            .updateState(stateUpdate)
            .map(_ ⇒ write(stateUpdate.anonymous))
        case _    ⇒ Future.successful(Types.noOp)
      }
  }

  override def isDefinedAt(x: Json): Boolean =
    Unpickle[StateUpdate].fromString(x).isSuccess
}
