package com.panetime.backend.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * Needed to prevent instance from optimized shutting down
  */
class KeepAlive(host: String, port: Int)
               (implicit system: ActorSystem, mat: ActorMaterializer,
                ctx: ExecutionContextExecutor) {

  def echoRequest(): Unit = Http()
    .singleRequest(HttpRequest(uri = s"http://$host:$port/${KeepAlive.echoRoute}"))
    .onComplete {
      case Success(response) ⇒ system.log.info("Echo request response: {}", response)
      case Failure(e)        ⇒ system.log.error("Echo failed: {}", e.getMessage)
    }
}

object KeepAlive {

  val echoRoute = "echo"

  def binding(host: String, port: Int)
             (implicit system: ActorSystem, mat: ActorMaterializer,
              ctx: ExecutionContextExecutor): Unit = {
    val keepAlive = new KeepAlive(host, port)
    system.scheduler.schedule(30 seconds, 5 minutes, () ⇒ keepAlive.echoRequest())
  }
}
