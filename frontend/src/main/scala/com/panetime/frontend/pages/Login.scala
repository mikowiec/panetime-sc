package com.panetime.frontend.pages

import com.panetime.frontend.pages.components.KeyStroke
import com.panetime.frontend.socket.ReplySocket
import com.panetime.frontend.util.draw
import com.panetime.model.Credentials
import org.scalajs.dom.Element
import org.scalajs.dom.document.getElementById
import org.scalajs.dom.html.{Button, Input}
import org.scalajs.dom.raw.MouseEvent

import scalatags.JsDom.all._

object Login {

  private val submitCredentials: MouseEvent ⇒ Unit =
    _ ⇒ ReplySocket.login(
      Credentials(
        getElementById("login").asInstanceOf[Input].value,
        getElementById("password").asInstanceOf[Input].value
      )
    )

  private val elements: List[Element] = List(
    div(`class` := "row",
      div(`class` := "col col-6 push-center",
        fieldset(
          legend("Credentials from secret e-mail:"),
          div(`class` := "form-item",
            label("Login"),
            input(`type` := "text", id := "login")
          ),
          div(`class` := "form-item",
            label("Password"),
            input(`type` := "password", id := "password")
          ),
          div(`class` := "form-item",
            button(id := "submit", "Submit", onclick := submitCredentials)
          )
        )
      )
    ).render
  )

  def render(): Unit = {
    KeyStroke.register({
      case e if e.keyCode == KeyStroke.ENTER ⇒
        getElementById("submit").asInstanceOf[Button].click()
    })
    draw(elements)
  }
}
