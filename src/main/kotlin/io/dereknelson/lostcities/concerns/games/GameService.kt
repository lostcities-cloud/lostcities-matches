package io.dereknelson.lostcities.concerns.games

import io.dereknelson.lostcities.concerns.matches.Match
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.LinkedHashSet

@Service
class GameService {

    @Autowired
    lateinit var deckFactory: DeckFactory

    private fun buildInitialGameState(match: Match) : GameState {
        val shuffledCards = deckFactory.buildDeck()
            .shuffled(Random(match.seed))

        val gameState = GameState(
            match.id,
            match.players,
            match.seed,
            LinkedHashSet(shuffledCards)
        )

        return gameState
    }
}