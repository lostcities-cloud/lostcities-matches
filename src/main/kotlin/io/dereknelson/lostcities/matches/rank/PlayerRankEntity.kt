package io.dereknelson.lostcities.matches.rank

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Table(
    name = "PlayerRankEntity",
    indexes = [
        Index(name = "player_index", columnList = "player", unique = false),
    ],
)
class PlayerRankEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "player")
    var player: String,

    @Column(name = "rank")
    var rank: Int = 1000,
)
