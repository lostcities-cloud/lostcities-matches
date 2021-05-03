package io.dereknelson.lostcities.concerns.matches

import io.dereknelson.lostcities.concerns.game.CommandRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
internal interface MatchRepository : JpaRepository<MatchEntity, Long> {

    companion object {
        const val MATCHES_BY_PLAYER_CACHE: String = "usersByPlayer"
    }

    @Cacheable(cacheNames = [MATCHES_BY_PLAYER_CACHE])
    @Query("""
        SELECT matchEntity 
        FROM MatchEntity matchEntity
        WHERE
            matchEntity.player1.id = :playerId OR 
            matchEntity.player2.id = :playerId
    """)
    fun getGamesByPlayerId(playerId: Long): List<MatchEntity>

    @Query("""
        SELECT matchEntity 
        FROM MatchEntity matchEntity
        WHERE
        (
            matchEntity.player1.id = :playerId OR 
            matchEntity.player2.id = :playerId
        ) AND
        matchEntity.isCompleted = true
    """)
    fun getCompletedGamesByPlayerId(playerId: Long): List<MatchEntity>

    @Query("""
        SELECT matchEntity 
        FROM MatchEntity matchEntity
        WHERE
        matchEntity.player1.id <> :playerId AND
        matchEntity.isReady = false
    """)
    fun getAvailableGamesForPlayer(playerId: Long): List<MatchEntity>

    @Query("""
        SELECT matchEntity 
        FROM MatchEntity matchEntity
        WHERE
        matchEntity.player1.id <> :playerId AND
        matchEntity.isReady = false
    """)
    fun getRecentlyCompletedGames(): List<MatchEntity>
}