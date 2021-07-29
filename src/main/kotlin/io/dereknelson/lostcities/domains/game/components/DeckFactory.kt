package io.dereknelson.lostcities.domains.game.components

import org.springframework.stereotype.Service
import java.util.stream.Collectors
import java.util.stream.IntStream

@Service
class DeckFactory {

    fun buildDeck() : List<Card> {
        val cards : MutableList<Card> = mutableListOf()

        Color.values().forEach {
            cards.addAll(buildCardsForColor(it))
        }

        return cards
    }

    private fun buildCardsForColor(color: Color) : List<Card> {
        val cards : MutableList<Card> = IntStream.range(1, 10)
            .mapToObj { Card(color, it) }
            .collect(Collectors.toList())

        cards.add(Card(color, 0, true))
        cards.add(Card(color, 0, true))

        return cards
    }
}