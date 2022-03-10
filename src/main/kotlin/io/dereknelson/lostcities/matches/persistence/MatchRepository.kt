package io.dereknelson.lostcities.matches.persistence

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : JpaRepository<MatchEntity, Long> {

    companion object {
        const val MATCHES_BY_PLAYER_CACHE: String = "usersByPlayer"
    }

    @Cacheable(cacheNames = [MATCHES_BY_PLAYER_CACHE])
    @Query(
    """
    SELECT matchEntity
    FROM MatchEntity matchEntity
    WHERE
        matchEntity.player1 = :playerName OR
        matchEntity.player2 = :playerName
    """
    )
    fun findActiveMatches(playerName: String, page: Pageable): Page<MatchEntity>

    @Query(
    """
    SELECT matchEntity
    FROM MatchEntity matchEntity
    WHERE
        (matchEntity.player1 <> :playerName AND matchEntity.player2 IS NULL) AND
        matchEntity.isReady = false AND
        matchEntity.isCompleted = false

    """
    )
    fun findAvailableMatches(playerName: String, page: Pageable): Page<MatchEntity>

    @Query("""
    SELECT matchEntity
    FROM MatchEntity matchEntity
    WHERE
        ( matchEntity.player1 = :playerName OR matchEntity.player2 = :playerName ) AND
        matchEntity.isCompleted = true
    """)
    fun findCompletedMatches(playerName: String, page: Pageable): Page<MatchEntity>
}
