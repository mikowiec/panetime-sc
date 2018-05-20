package com.panetime.model

import com.panetime.model.Types.Login

case class Token(login: Login, value: String) {

  def anonymous: Token = copy(value = "")
}
