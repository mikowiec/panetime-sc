package com.panetime.model

import com.panetime.model.Types.Login

case class GroupState(data: Map[Login, Set[SelectedDate]] = Map.empty)