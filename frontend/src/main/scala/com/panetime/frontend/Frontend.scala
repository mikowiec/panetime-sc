package com.panetime.frontend

import com.panetime.frontend.pages.Login
import com.panetime.frontend.socket.{BroadcastSocket, ReplySocket}

import scala.scalajs.js

object Frontend extends js.JSApp {

  override def main(): Unit = {
    ReplySocket.connect()
    BroadcastSocket.connect()
    ReplySocket.loginWithToken()
    Login.render()
  }
}
