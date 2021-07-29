package io.dereknelson.lostcities.domains.game.components

data class Card(
    val color: Color,
    val value: Int,
    val isMultiplier: Boolean = false
)
