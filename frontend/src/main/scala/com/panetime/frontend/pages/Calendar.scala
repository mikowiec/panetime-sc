package com.panetime.frontend.pages

import com.panetime.frontend.pages.components.CalendarSelection
import com.panetime.frontend.socket.BroadcastSocket
import com.panetime.frontend.socket.storage.LoginStorage
import com.panetime.frontend.util.{JsDateUtils, MonthOffset, daysOfYear, draw, offsetMonths}
import com.panetime.model.Types.Login
import com.panetime.model.{GroupState, SelectedDate, StateUpdate}
import org.scalajs.dom.raw.MouseEvent

import scala.scalajs.js.Date
import scalatags.JsDom.all._

object Calendar {

  def renderCalendarView(state: GroupState): Unit = {
    val start = startDate

    draw(
      List(
        div(`class` := "row", style := "padding: 1%",
          div(`class` := "col col-5 push-center", style := "text-align: center",
            h2("Vacation calendar")
          ),
        ),
        div(`class` := "row",
          div(`class` := "row", calendarTable(state, start))
        )
      ).map(_.render)
    )

    state.data.foreach {
      case (login, dates) ⇒
        dates
          .filter(_.date <= start.getTime())
          .foreach(date ⇒ update(login, date, selected = true))
    }
  }

  def update(delta: StateUpdate): Unit =
    update(delta.token.login, delta.date, delta.selected)

  private def update(login: Login,
                     selectedDate: SelectedDate,
                     selected: Boolean): Unit = {
    val id = CalendarSelection.cellId(login, selectedDate.date)
    if (selected) CalendarSelection.select(id)
    else CalendarSelection.unSelect(id)
  }

  private def calendarTable(state: GroupState, start: Date) = {
    val dates = generateDates(start)
    val months = offsetMonths(dates)

    table(`class` := "bordered",
      tr(style := "text-align: center", td(), months.map(monthCell)),
      state.data.keys.toList.sorted
        .map(login ⇒ login -> dates)
        .map {
          case (login, dateLine) ⇒
            tr(
              td(b(login)),
              dateLine.map(date ⇒
                dateCell(login, date, LoginStorage.token.login == login)
              )
            )
        }
        .toList
    )
  }

  private def monthCell(month: MonthOffset) = td(colspan := month._2, month._1)

  private def dateCell(login: Login, date: Date, canModify: Boolean) = {
    val identifier = CalendarSelection.cellId(login, date)
    val pointer = if (canModify) "cursor: pointer;" else ""
    val onclickFunction: MouseEvent ⇒ Unit =
      if (canModify) dateClick(identifier) else _ ⇒ Unit
    td(
      id := identifier,
      style := s"white-space: nowrap; $pointer",
      date.dayOfWeek,
      onclick := onclickFunction
    )
  }

  private def dateClick(identifier: String): MouseEvent ⇒ Unit =
    _ ⇒ BroadcastSocket.modifyDate(
      SelectedDate(CalendarSelection.cellUTCDate(identifier)),
      !CalendarSelection.isSelected(identifier)
    )

  private def generateDates(start: Date) =
    daysOfYear(start, start.plusYears(1), List(start))

  private def startDate = {
    val start = new Date()
    start.setHours(0, 0, 0, 0)
    start
  }
}
