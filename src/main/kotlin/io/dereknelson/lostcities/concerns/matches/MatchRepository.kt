package io.dereknelson.lostcities.concerns.matches

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
internal interface MatchRepository : JpaRepository<MatchEntity, Long> {

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