package io.dereknelson.lostcities.api.game

import io.dereknelson.lostcities.domains.game.components.Card
import io.dereknelson.lostcities.domains.game.components.Color
import io.dereknelson.lostcities.domains.game.components.Phase

data class CommandDto(
    val playerId: Long,
    val phase: Phase,
    val draw: Boolean,
    val discard: Boolean,
    val color: Color?,
    val card: Card?
)