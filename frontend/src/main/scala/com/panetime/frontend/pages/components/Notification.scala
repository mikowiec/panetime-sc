package com.panetime.frontend.pages.components

import org.scalajs.dom.{Node, document, window}

import scalatags.JsDom.all._

object Notification {

  def info(text: String): Unit = display(text, "success")

  def error(text: String): Unit = display(text, "error")

  private def display(text: String, status: String): Unit = {
    val notificationElement = element(text, status)
    document.body.appendChild(notificationElement)
    window.setTimeout(() ⇒ remove(notificationElement), 2000)
  }

  private def remove(notification: Node): Unit =
    try {
      document.body.removeChild(notification)
    } catch {
      case _: Exception ⇒
    }

  private def element(content: String, status: String): Node =
    div(`class` := s"message $status", content, span(`class` := "close small")).render
}
