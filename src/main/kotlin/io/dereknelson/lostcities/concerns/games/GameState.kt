package io.dereknelson.lostcities.concerns.games

import io.dereknelson.lostcities.concerns.matches.UserPair
import io.dereknelson.lostcities.concerns.users.User
import java.util.*
import kotlin.collections.LinkedHashSet

class GameState(
    val id : Long,
    val players : UserPair,
    var initialSeed : Long,
    val deck : LinkedHashSet<Card>
) {
    private val random : Random = Random(initialSeed)

    private val discard = PlayArea()

    private val playerAreas: Map<Long, PlayArea> = mapOf(
        players.user1?.id!! to PlayArea(),
        players.user2?.id!! to PlayArea()
    )
    private val playerHands: Map<Long, MutableList<Card>> = mapOf(
        players.user1?.id!! to mutableListOf(),
        players.user2?.id!! to mutableListOf()
    )

    var phase = Phase.PLAY_OR_DISCARD

    var currentPlayer : User = players.user1!!

    fun nextSeed() : Long {
        return random.nextLong()
    }

    fun nextPhase() {
        phase = if(phase == Phase.PLAY_OR_DISCARD) {
            Phase.DRAW
        } else {
            Phase. PLAY_OR_DISCARD
        }
    }

    fun drawCard(playerId: Long) {
        if(deck.isNotEmpty()) {
            val drawn = deck.first()
            deck.remove(drawn)
            getHand(playerId).add(drawn)
        }
    }

    fun canDrawFromDiscard(color : Color) : Boolean {
        return !discard.isEmpty(color)
    }

    fun isCardInHand(playerId : Long, card : Card) : Boolean {
        return getHand(playerId).contains(card)
    }

    fun drawFromDiscard(playerId : Long, color: Color) {
        if(canDrawFromDiscard(color)) {
            val cards = discard.get(color)
            val drawn = cards.first()
            cards.remove(drawn)
            getHand(playerId).add(drawn)
        }
    }

    fun playCard(playerId : Long, card : Card) {
        if(isCardInHand(playerId, card)) {
            getPlayerArea(playerId).get(card.color).add(card)
        }
    }

    fun discard(playerId : Long, card : Card) {
        if(isCardInHand(playerId, card)) {
            discard.get(card.color).add(card)
        }
    }

    private fun getHand(playerId : Long) : MutableList<Card> {
        return playerHands[playerId]!!
    }

    private fun getPlayerArea(playerId : Long) : PlayArea {
        return playerAreas[playerId]!!
    }

}
