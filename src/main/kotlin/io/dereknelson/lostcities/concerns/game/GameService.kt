package io.dereknelson.lostcities.concerns.game

import io.dereknelson.lostcities.concerns.game.components.DeckFactory
import io.dereknelson.lostcities.concerns.matches.Match
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.LinkedHashSet

@Service
class GameService {

    @Autowired
    private lateinit var deckFactory: DeckFactory

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