package io.dereknelson.lostcities.concerns.games

class PlayArea {
    val board: Map<Color, MutableList<Card>> = Color.values()
        .associate { it to mutableListOf() }

    fun get(color: Color) : MutableList<Card> {
        return board[color]!!
    }

    fun add(card: Card) {
        get(card.color).add(card)
    }

    fun peak(color: Color) : Card? {
        return if(isEmpty(color)) {
            null
        } else {
            get(color).last()
        }
    }

    fun isEmpty(color : Color) : Boolean {
        return board[color]?.isEmpty()!!
    }
}