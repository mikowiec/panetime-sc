package com.panetime.frontend.socket

import com.panetime.frontend.pages.components.Notification
import com.panetime.model.Forbidden
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.document.location
import org.scalajs.dom.raw.{Event, WebSocket}
import prickle.Unpickle

trait Socket {

  private var underlying = refreshSocket()

  def connect(): Unit = {
    underlying = refreshSocket()

    underlying.onmessage = {
      (e: MessageEvent) ⇒ {
        val json = e.data.toString
        onForbidden(json) { forbidden ⇒ Notification.error(forbidden.message) }
        onMessage(json)
      }
    }
    underlying.onclose = (_: Event) ⇒ connect()
  }

  def connected: Boolean = underlying.readyState == 1

  private def refreshSocket(): WebSocket = new WebSocket(socketUrl(name))

  private def onForbidden(json: String)(callback: Forbidden ⇒ Unit): Unit =
    Unpickle[Forbidden].fromString(json).foreach(callback)

  private def socketUrl(name: String): String = {
    val port = location.port
    val portString = if (port.isEmpty) "" else s":$port"
    s"ws://${location.hostname}$portString/$name"
  }

  def send(json: String): Unit = underlying.send(json)

  def onMessage(json: String): Unit

  def name: String
}