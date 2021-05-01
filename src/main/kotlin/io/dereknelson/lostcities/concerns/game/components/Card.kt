package io.dereknelson.lostcities.concerns.game.components

data class Card(
    val color: Color,
    val value: Int,
    val isMultiplier: Boolean = false
)
