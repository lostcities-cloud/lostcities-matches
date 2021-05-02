package io.dereknelson.lostcities.concerns.game

import io.dereknelson.lostcities.concerns.game.entities.CommandEntity
import io.dereknelson.lostcities.concerns.users.entity.UserEntity
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
internal interface CommandRepository : JpaRepository<CommandEntity, Long> {

    fun findByGameId(matchId: Long): List<CommandEntity>
}