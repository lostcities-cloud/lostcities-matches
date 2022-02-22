package io.dereknelson.lostcities.matches.persistence

import io.dereknelson.lostcities.common.library.AbstractAuditingEntity
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
    name = "match",
    indexes = [
        Index(name = "player_1_index", columnList = "player_1", unique = false),
        Index(name = "player_2_index", columnList = "player_2", unique = false),
    ]
)
class MatchEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "seed")
    var seed: Long,

    @Column(name = "player_1")
    var player1: String,

    @Column(name = "player_2")
    var player2: String? = null,

    @Column(name = "current_turn")
    var currentPlayer: String? = null,

    @Column(name = "score_1")
    var score1: Int = 0,

    @Column(name = "score_2")
    var score2: Int = 0,

    @Column(name = "conceded_by")
    var concededBy: String? = null,

    @Column(name = "is_ready")
    var isReady: Boolean = false,

    @Column(name = "is_started")
    var isStarted: Boolean = false,

    @Column(name = "is_completed")
    var isCompleted: Boolean = false
) : AbstractAuditingEntity(), Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
