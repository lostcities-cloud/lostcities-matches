package io.dereknelson.lostcities.matches.archive

import jakarta.persistence.*

@Entity
@Table(
    name = "MatchArchiveEntity",
)
class MatchArchiveEntity(
    @Id
    var id: Long,

    @Column(name = "seed")
    var match: String,
)
