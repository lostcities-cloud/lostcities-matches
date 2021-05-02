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

    @Column(name="seed")
    var seed: Long? = null,

    @ManyToOne
    var player1: UserRef? = null,

    @ManyToOne
    var player2: UserRef? = null,

    @Column(name="is_ready")
    var isReady: Boolean? = false,

    @Column(name="is_started")
    var isStarted: Boolean? = false,

    @Column(name="is_completed")
    var isCompleted: Boolean? = false
) {
    @Column(name="created_date")
    @CreatedDate
    var createdDate: Timestamp? = null

    @Column(name="last_modified_date")
    @LastModifiedDate
    var lastModifiedDate: Timestamp? = null

    @Column(name="created_by")
    @CreatedBy
    var createdBy: Long? = null
}