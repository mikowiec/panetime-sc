package com.panetime.frontend.pages.components

import org.scalajs.dom.{KeyboardEvent, document}

object KeyStroke {

  val ENTER = 13
  private[this] val acceptAll: KeyboardEvent ⇒ Unit = _ ⇒ Unit

  def register(pf: PartialFunction[KeyboardEvent, Unit]): Unit = {
    document.onkeydown =
      Option(document.onkeydown)
        .map(f ⇒ combine(pf, f))
        .getOrElse(combine(pf, acceptAll))
  }

  def clear(): Unit = document.onkeydown = null

  private def combine(pf: PartialFunction[KeyboardEvent, Unit],
                      f: KeyboardEvent ⇒ _): KeyboardEvent ⇒ _ =
    pf.orElse({ case s ⇒ f(s) })
}
