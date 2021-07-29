package io.dereknelson.lostcities.domains.game

import io.dereknelson.lostcities.domains.game.components.Card
import io.dereknelson.lostcities.domains.game.components.Color
import io.dereknelson.lostcities.domains.game.components.Phase
import io.dereknelson.lostcities.domains.game.components.PlayArea
import io.dereknelson.lostcities.domains.matches.UserPair
import io.dereknelson.lostcities.common.User
import kotlin.collections.LinkedHashSet

class GameState(
    val id : Long,
    players : UserPair,
    private val deck : LinkedHashSet<Card>
) {
    var phase = Phase.PLAY_OR_DISCARD
    var currentPlayer : User = players.user1!!

    private val discard = PlayArea()

    private val playerAreas: Map<String, PlayArea> = mapOf(
        players.user1?.login!! to PlayArea(),
        players.user2?.login!! to PlayArea()
    )

    private val playerHands: Map<String, MutableList<Card>> = mapOf(
        players.user1?.login!! to mutableListOf(),
        players.user2?.login!! to mutableListOf()
    )

    fun nextPhase() {
        phase = if(phase == Phase.PLAY_OR_DISCARD) {
            Phase.DRAW
        } else {
            Phase.PLAY_OR_DISCARD
        }
    }

    fun drawCard(player: String) {
        if(deck.isNotEmpty()) {
            val drawn = deck.first()
            deck.remove(drawn)
            getHand(player).add(drawn)
        }
    }

    fun drawFromDiscard(player : String, color: Color) {
        if(canDrawFromDiscard(color)) {
            val cards = discard.get(color)
            val drawn = cards.first()
            cards.remove(drawn)
            getHand(player).add(drawn)
        }
    }

    fun playCard(player : String, card : Card) {
        if(isCardInHand(player, card)) {
            getPlayerArea(player).get(card.color).add(card)
        }
    }

    fun discard(player : String, card : Card) {
        if(isCardInHand(player, card)) {
            discard.get(card.color).add(card)
        }
    }

    private fun canDrawFromDiscard(color : Color) : Boolean {
        return !discard.isEmpty(color)
    }

    private fun isCardInHand(player : String, card : Card) : Boolean {
        return getHand(player).contains(card)
    }

    private fun getHand(player : String) : MutableList<Card> {
        return playerHands[player]!!
    }

    private fun getPlayerArea(player : String) : PlayArea {
        return playerAreas[player]!!
    }

}
