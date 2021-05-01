package io.dereknelson.lostcities.concerns.matches

import io.dereknelson.lostcities.concerns.users.UserRef
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name="match_entity")
data class MatchEntity(
    @Id
    var id: Long? = null,

    var seed: Long? = null,

    @ManyToOne
    var player1: UserRef? = null,

    @ManyToOne
    var player2: UserRef? = null,

    var isReady: Boolean? = false,
    var isStarted: Boolean? = false,
    var isCompleted: Boolean? = false
) {
    @CreatedDate
    val createdDate: Timestamp? = null

    @LastModifiedDate
    val lastModifiedDate: Timestamp? = null

    @CreatedBy
    var createdBy: Long? = null
}