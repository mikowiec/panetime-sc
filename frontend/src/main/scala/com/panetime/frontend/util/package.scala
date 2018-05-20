package com.panetime.frontend

import org.scalajs.dom.{Element, document}

import scala.annotation.tailrec
import scala.scalajs.js.Date

package object util {

  type MonthOffset = (String, Int)

  def draw(elements: List[Element]): Unit = {
    document.body.innerHTML = ""
    elements.foreach(document.body.appendChild)
  }

  def draw(element: Element): Unit = draw(element :: Nil)

  implicit class JsDateUtils(jsDate: Date) {

    def dayOfWeek = s"${jsDate.getDate()}, ${daysOfWeek(jsDate.getDay())}"

    def month = months(jsDate.getMonth())

    def plusYears(years: Int): Date = {
      new Date(
        jsDate.getFullYear() + years,
        jsDate.getMonth(),
        jsDate.getDate()
      )
    }

    def id = s"${jsDate.getDay()}/${jsDate.getMonth()}/${jsDate.getFullYear()}"
  }

  @tailrec
  def daysOfYear(start: Date,
                 finish: Date,
                 acc: List[Date] = List.empty): List[Date] = {
    if (start.valueOf() < finish.valueOf()) {
      start.setDate(start.getDate() + 1)
      daysOfYear(start, finish, new Date(start.valueOf()) :: acc)
    }
    else acc.reverse
  }

  @tailrec
  def offsetMonths(dates: List[Date],
                   currentMonthSize: Int = 1,
                   lastMonth: Option[String] = None,
                   acc: List[MonthOffset] = List.empty): List[MonthOffset] =
    dates match {
      case head :: tail ⇒
        val month = head.month
        if (lastMonth.contains(month)) {
          val monthSize = currentMonthSize + 1
          offsetMonths(
            tail, monthSize, Option(month), (month, monthSize) :: acc.tail
          )
        } else offsetMonths(tail, 1, Option(month), (month, 1) :: acc)
      case _ ⇒ acc.reverse
    }


  private val daysOfWeek = Array("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

  private val months = Array("January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December")
}
