package io.dereknelson.lostcities.concerns.game.entities

import io.dereknelson.lostcities.concerns.game.components.Color
import io.dereknelson.lostcities.concerns.game.components.Phase
import javax.persistence.*

@Entity
@Table(
        name = "command",
        indexes = [Index(name="match_id", columnList="match_id", unique=false)]
)
data class CommandEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name="match_id")
    val matchId: Long,

    @Column(name="player")
    val player: String,

    @Column(name="phase")
    val phase: Phase,

    @Column(name="draw")
    val draw: Boolean,

    @Column(name="discard")
    val discard: Boolean,

    @Column(name="color")
    val color: Color?,

    @Column(name="card_color")
    val cardColor: Color?,

    @Column(name="card_value")
    val cardValue: Int?
)
