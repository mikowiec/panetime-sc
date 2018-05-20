package com.panetime.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.panetime.backend.service.chain._
import com.panetime.backend.service.database.{Db, StateRepository, UserRepository}
import com.panetime.backend.service.{KeepAlive, Rest, Socket}

import scala.util.{Failure, Success, Try}

object Backend extends App {

  val host = Try(args(0).toString).getOrElse("0.0.0.0")
  val port = Try(sys.env("PORT").toInt).getOrElse(4567)

  Db.init()

  implicit val system: ActorSystem = ActorSystem("panetime-system")

  import system.dispatcher

  implicit val mat: ActorMaterializer = ActorMaterializer()

  val credentialsProcess = new CredentialsProcess(UserRepository)
  val stateRequestProcess = new StateRequestProcess(StateRepository)
  val stateUpdateProcess = new StateUpdateProcess(StateRepository, UserRepository)
  val tokenCheckProcess = new TokenCheckProcess(UserRepository)

  val processing =
    stateUpdateProcess
      .orElse(stateRequestProcess)
      .orElse(UserOnlineProcess)
      .orElse(credentialsProcess)
      .orElse(tokenCheckProcess)

  val socket = Socket(processing)
  val rest = Rest(KeepAlive.echoRoute, socket.replyFlow, socket.broadcastFlow)

  val binding = Http().bindAndHandle(rest.route, host, port)

  binding.onComplete {
    case Success(_) ⇒
      KeepAlive.binding(host, port)
      system.log.info("Server is listening to {}:{}", host, port)
    case Failure(e) ⇒
      system.log.error(e, "Could not start server at {}:{}", host, port)
  }
}
