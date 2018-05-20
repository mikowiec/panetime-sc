package com.panetime.backend.service.chain

import scala.concurrent.Future

object Types {

  type Json = String

  type JsonReply = Future[String]

  val noOp = ""
}
