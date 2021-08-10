package io.dereknelson.lostcities.commandJobProcessor.game

import io.dereknelson.lostcities.common.model.game.GameState
import io.dereknelson.lostcities.common.model.match.Match
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
            LinkedHashSet(shuffledCards)
        )
    }
}