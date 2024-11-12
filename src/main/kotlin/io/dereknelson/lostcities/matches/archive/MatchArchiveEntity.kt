package io.dereknelson.lostcities.matches.archive

import com.fasterxml.jackson.annotation.JsonIgnore
import io.dereknelson.lostcities.common.library.AbstractAuditingEntity
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

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
