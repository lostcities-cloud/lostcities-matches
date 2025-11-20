package io.dereknelson.lostcities.matches.archive

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(
    name = "MatchArchiveEntity",
)
class MatchArchiveEntity(
    @Id
    var id: Long,
    @Column(name = "seed", nullable = false)
    var match: String = "undefined-seed",
)
