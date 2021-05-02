package io.dereknelson.lostcities.concerns.game.entities

import io.dereknelson.lostcities.concerns.game.components.Color
import io.dereknelson.lostcities.concerns.game.components.Phase
import javax.persistence.*

@Entity
@Table(
        name = "command",
        indexes = [Index(name="gameId", columnList="gameId", unique=false)]
)
data class CommandEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name="game_id")
    val gameId: Long,

    @Column(name="player_id")
    val playerId: Long,

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
