package io.dereknelson.lostcities.matches.persistence

import io.dereknelson.lostcities.common.auth.entity.UserRef
import io.dereknelson.lostcities.matches.library.security.AbstractAuditingEntity
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(
    name="match",
    indexes = [
        Index(name="player_1_index", columnList="player_1", unique=false),
        Index(name="player_2_index", columnList="player_2", unique=false),
    ]
)
data class MatchEntity (

    @Id
    var id: Long? = null,

    @Column(name = "seed")
    var seed: Long,

    @Column(name="player_1")
    var player1: Long? = null,

    @Column(name="player_2")
    var player2: Long? = null,

    @Column(name = "score_1")
    var score1: Int = 0,

    @Column(name = "score_2")
    var score2: Int = 0,

    @Column(name = "conceded_by")
    var concededBy: Long? = null,

    @Column(name = "is_ready")
    var isReady: Boolean = false,

    @Column(name = "is_started")
    var isStarted: Boolean = false,

    @Column(name = "is_completed")
    var isCompleted: Boolean = false,
    ): AbstractAuditingEntity(), Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}