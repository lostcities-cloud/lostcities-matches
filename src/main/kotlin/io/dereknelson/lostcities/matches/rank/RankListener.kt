package io.dereknelson.lostcities.matches.rank

import io.dereknelson.lostcities.matches.FinishGameEvent
import io.dereknelson.lostcities.matches.FinishGameScore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import kotlin.math.pow

@Component
class RankListener(
    @Autowired
    val rankRepository: RankRepository,
) : ApplicationListener<FinishGameEvent> {
    override fun onApplicationEvent(event: FinishGameEvent) {
        val score: FinishGameScore = event.source as FinishGameScore
        val player1Rank: PlayerRankEntity = rankRepository.findPlayerRankEntityByPlayer(score.player1Name)
            .orElse(PlayerRankEntity(player = score.player1Name))
        val player2Rank: PlayerRankEntity = rankRepository.findPlayerRankEntityByPlayer(score.player2Name)
            .orElse(PlayerRankEntity(player = score.player2Name))

        if (score.player1Score == score.player2Score) {
            calculateAndSaveRank(score.player1Name, player1Rank, player2Rank, "=")
            calculateAndSaveRank(score.player2Name, player2Rank, player2Rank, "=")
        } else if (score.player1Score > score.player2Score) {
            calculateAndSaveRank(score.player1Name, player1Rank, player2Rank, "+")
            calculateAndSaveRank(score.player2Name, player2Rank, player2Rank, "-")
        } else {
            calculateAndSaveRank(score.player1Name, player1Rank, player2Rank, "-")
            calculateAndSaveRank(score.player2Name, player2Rank, player2Rank, "+")
        }
    }

    fun calculateAndSaveRank(
        player: String,
        player1Rank: PlayerRankEntity,
        player2Rank: PlayerRankEntity,
        outcome: String,
    ) {
        val rank = calculate2PlayersRating(player1Rank.rank, player2Rank.rank, "=")
        player1Rank.rank = rank
        rankRepository.save(player1Rank)
    }

    fun calculate2PlayersRating(player1Rating: Int, player2Rating: Int, outcome: String): Int {
        // winner
        val actualScore = if (outcome == "+") {
            1.0
            // draw
        } else if (outcome == "=") {
            0.5
            // lose
        } else if (outcome == "-") {
            0.0
            // invalid outcome
        } else {
            return player1Rating
        }

        // calculate expected outcome
        val exponent = (player2Rating - player1Rating).toDouble() / 400
        val expectedOutcome: Double = (1 / (1 + (10.toDouble().pow(exponent))))

        // K-factor
        val K = 32

        // calculate new rating
        val newRating = Math.round(player1Rating + K * (actualScore - expectedOutcome)).toInt()

        return newRating
    }

    /**
     * Determine the rating constant K-factor based on current rating
     *
     * @param rating
     * Player rating
     * @return K-factor
     */
    fun determineK(rating: Int): Int {
        val K = if (rating < 2000) {
            32
        } else if (rating >= 2000 && rating < 2400) {
            24
        } else {
            16
        }
        return K
    }
}
