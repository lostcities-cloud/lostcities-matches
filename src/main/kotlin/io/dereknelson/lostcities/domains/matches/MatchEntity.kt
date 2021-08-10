package io.dereknelson.lostcities.domains.matches

import io.dereknelson.lostcities.common.model.UserRef
import io.dereknelson.lostcities.library.security.AbstractAuditingEntity
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
    var seed: Long? = null,

    @ManyToOne
    @JoinColumn(
        name = "player_1",
        nullable = false,
        foreignKey = ForeignKey(name="PLAYER_1_FOREIGN_KEY")
    )
    var player1: UserRef? = null,

    @ManyToOne
    @JoinColumn(
        name = "player_2",
        nullable = true,
        foreignKey = ForeignKey(name="PLAYER_2_FOREIGN_KEY")
    )
    var player2: UserRef? = null,

    @Column(name = "score_1")
    var score1: Int? = null,

    @Column(name = "score_2")
    var score2: Int? = null,

    @ManyToOne
    @JoinColumn(
        name = "conceded_by",
        nullable = true,
        foreignKey = ForeignKey(name="CONCEDED_BY_FOREIGN_KEY")
    )
    var concededBy: UserRef? = null,

    @Column(name = "is_ready")
    var isReady: Boolean? = false,

    @Column(name = "is_started")
    var isStarted: Boolean? = false,

    @Column(name = "is_completed")
    var isCompleted: Boolean? = false,
    ): AbstractAuditingEntity(), Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}