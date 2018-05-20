package com.panetime.backend.service.repository

import com.panetime.model.{GroupState, StateUpdate, Token}

import scala.concurrent.Future

trait StateRepository {

  def getState(token: Token): Future[GroupState]

  def updateState(stateUpdate: StateUpdate): Future[Unit]
}