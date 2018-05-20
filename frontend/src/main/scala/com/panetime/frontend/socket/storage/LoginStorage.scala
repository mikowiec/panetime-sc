package com.panetime.frontend.socket.storage

import com.panetime.model.Token
import com.panetime.model.Types.Login
import org.scalajs.dom.window

object LoginStorage {

  private val applicationPrefix = "com.panetime-"

  def store(token: Token): Unit =
    window.sessionStorage.setItem(loginAsKey(token.login), token.value)

  def token: Token =
    (0 until window.sessionStorage.length)
      .map(window.sessionStorage.key)
      .find(isApplicationPrefix)
      .map(key ⇒ Token(keyAsLogin(key), window.sessionStorage.getItem(key)))
      .get

  private def loginAsKey(login: Login) = s"$applicationPrefix$login"

  private def keyAsLogin(key: String) = key.replace(applicationPrefix, "")

  private def isApplicationPrefix(key: String) = key.startsWith(applicationPrefix)

  def clear(): Unit = window.sessionStorage.clear()
}