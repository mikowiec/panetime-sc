package com.panetime.frontend.socket

import com.panetime.frontend.pages.Calendar
import com.panetime.frontend.pages.components.KeyStroke
import com.panetime.frontend.socket.storage.LoginStorage
import com.panetime.model.{Credentials, GroupState, StateRequest, Token}
import prickle.Pickle.{intoString ⇒ write}
import org.scalajs.dom.window
import prickle.Unpickle

import scala.util.{Success, Try}

object ReplySocket extends Socket {

  override def name = "reply"

  def loginWithToken(): Unit = {
    window.setTimeout(() ⇒ {
      Try(LoginStorage.token) match {
        case Success(token) ⇒
          if (connected) send(write(token))
        case _              ⇒ Unit
      }
    }, 500)
  }

  def login(credentials: Credentials): Unit = send(write(credentials))

  def requestInitialState(token: Token): Unit = send(write(StateRequest(token)))

  override def onMessage(json: String): Unit = {
    onTokenUpdate(json)(token ⇒ {
      LoginStorage.clear()
      LoginStorage.store(token)
      BroadcastSocket.reportOnline(token.login)
      requestInitialState(token)
    })
    onInitialState(json) {
      state ⇒
        KeyStroke.clear()
        Calendar.renderCalendarView(state)
    }
  }

  private def onTokenUpdate(json: String)(callback: Token ⇒ Unit): Unit = {
    Unpickle[Token].fromString(json).foreach(callback)
  }

  private def onInitialState(json: String)(callback: GroupState ⇒ Unit): Unit = {
    Unpickle[GroupState].fromString(json).foreach(callback)
  }
}
