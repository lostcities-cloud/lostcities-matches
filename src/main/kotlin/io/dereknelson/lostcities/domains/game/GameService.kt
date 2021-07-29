package io.dereknelson.lostcities.domains.game

import io.dereknelson.lostcities.domains.game.components.DeckFactory
import io.dereknelson.lostcities.domains.matches.Match
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.LinkedHashSet

@Service
class GameService(
    private var deckFactory: DeckFactory
) {

    fun constructStateFromMatch(match: Match) : GameState {
        val shuffledCards = deckFactory.buildDeck()
            .shuffled(Random(match.seed))

        return GameState(
            match.id,
            match.players,
            match.seed,
            LinkedHashSet(shuffledCards)
        )
    }
}