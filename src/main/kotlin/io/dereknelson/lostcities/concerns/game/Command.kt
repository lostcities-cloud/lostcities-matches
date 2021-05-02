package io.dereknelson.lostcities.concerns.game

import io.dereknelson.lostcities.concerns.game.components.Card
import io.dereknelson.lostcities.concerns.game.components.Color
import io.dereknelson.lostcities.concerns.game.components.Phase

data class Command (
    val gameId: Long,
    val playerId: Long,
    val phase: Phase,
    val draw: Boolean,
    val discard: Boolean,
    val color: Color?,
    val card: Card?
) {
    fun validate(): Boolean {
        return if(phase == Phase.PLAY_OR_DISCARD) {
            !draw && card != null
        } else if (phase == Phase.DRAW) {
            draw && (color != null)
        } else {
            false
        }
    }
}