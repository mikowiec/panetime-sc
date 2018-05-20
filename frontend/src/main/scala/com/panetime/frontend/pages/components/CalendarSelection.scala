package com.panetime.frontend.pages.components

import com.panetime.model.Types
import com.panetime.model.Types.Login
import org.scalajs.dom.document

import scala.scalajs.js.Date
import scala.util.Try

object CalendarSelection {

  private val delimiter = "-"
  private val selectedClass = "success"

  def isSelected(identifier: String): Boolean =
    Try(document.getElementById(identifier).classList)
      .map(_.contains(selectedClass))
      .fold(_ ⇒ false, identity)

  def cellId(login: Login, date: Date): String =
    s"$login$delimiter${date.getTime().toLong}"

  def cellId(login: Login, date: Types.Date): String =
    s"$login$delimiter$date"

  def cellUTCDate(identifier: String): Types.Date =
    identifier.split(delimiter)(1).toLong

  def select(identifier: String): Unit =
    if (!isSelected(identifier))
      Try(document.getElementById(identifier).classList)
        .foreach(_.add(selectedClass))

  def unSelect(identifier: String): Unit =
    Try(document.getElementById(identifier).classList)
      .foreach(_.remove(selectedClass))
}
