package io.dereknelson.lostcities.concerns.games

data class Card(
    val color: Color,
    val value: Int,
    val isMultiplier: Boolean = false
)
