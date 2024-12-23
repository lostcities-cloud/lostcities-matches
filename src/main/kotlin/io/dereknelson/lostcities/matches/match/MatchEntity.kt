package io.dereknelson.lostcities.matches.match

import com.fasterxml.jackson.annotation.JsonIgnore
import io.dereknelson.lostcities.common.auditing.AbstractAuditingEntity
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(
    name = "MatchEntity",
    indexes = [
        Index(name = "player_1_index", columnList = "player_1", unique = false),
        Index(name = "player_2_index", columnList = "player_2", unique = false),
    ],
)
class MatchEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "seed")
    var seed: Long,

    @JsonIgnore
    @Column(name = "match_rank")
    var matchRank: Int = 0,

    @JsonIgnore
    @Column(name = "match_make_count")
    var matchMakingCount: Int? = 0,

    @Column(name = "player_1")
    var player1: String,

    @Column(name = "player_2")
    var player2: String? = null,

    @Column(name = "player_1_ai")
    var isPlayer1Ai: Boolean? = false,

    @Column(name = "player_2_ai")
    var isPlayer2Ai: Boolean? = false,

    @Column(name = "current_turn")
    var currentPlayer: String? = null,

    @JsonIgnore
    @Column(name = "turns")
    var turns: Long = 0,

    @Column(name = "score_1")
    var score1: Int = 0,

    @Column(name = "score_2")
    var score2: Int = 0,

    @JsonIgnore
    @Column(name = "conceded_by")
    var concededBy: String? = null,

    @JsonIgnore
    @Column(name = "is_ready")
    var isReady: Boolean = false,

    @JsonIgnore
    @Column(name = "is_started")
    var isStarted: Boolean = false,

    @JsonIgnore
    @Column(name = "is_completed")
    var isCompleted: Boolean = false,

    @JsonIgnore
    @Column(name = "finished_at")
    var finishedAt: LocalDateTime? = null,
) : AbstractAuditingEntity(), Serializable {
    companion object {
        private const val serialVersionUID = 1L

        fun buildMatch(player: String, seed: Long): MatchEntity {
            return MatchEntity(
                player1 = player,
                seed = seed,
            )
        }
    }

    fun randomizePlayers() {
        if (isReady) {
            throw RuntimeException("Cannot randomize players after game has started.")
        }

        if (player2 == null) {
            throw RuntimeException("Cannot randomize a game without 2 players.")
        }

        val players = listOf(
            PlayerObj(player1, if (isPlayer1Ai != null) isPlayer1Ai!! else false),
            PlayerObj(player2!!, if (isPlayer2Ai != null) isPlayer2Ai!! else false),
        ).shuffled()

        player1 = players[0].player
        isPlayer1Ai = players[0].aiPlayer
        player2 = players[1].player
        isPlayer2Ai = players[1].aiPlayer

        currentPlayer = player1
    }

    fun hasPlayer(name: String) = player1 == name || player2 == name
}

data class PlayerObj(val player: String, val aiPlayer: Boolean)
