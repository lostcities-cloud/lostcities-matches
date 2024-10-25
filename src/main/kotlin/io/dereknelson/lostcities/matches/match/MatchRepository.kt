package io.dereknelson.lostcities.matches.match

import io.dereknelson.lostcities.matches.Constants
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : JpaRepository<MatchEntity, Long> {

    companion object {
        const val MATCHES_BY_PLAYER_CACHE: String = "usersByPlayer"
    }

    // @Cacheable(cacheNames = [MATCHES_BY_PLAYER_CACHE])
    @Query(
        """
    SELECT matchEntity
    FROM MatchEntity matchEntity
    WHERE
        (
            matchEntity.player1 = :playerName OR matchEntity.player2 = :playerName
        ) AND
        matchEntity.isReady = TRUE
    ORDER BY matchEntity.lastModifiedDate
        """,
    )
    fun findActiveMatches(playerName: String, page: Pageable): Page<MatchEntity>

    @Query(
        value = """SELECT matchEntity
    FROM MatchEntity matchEntity
    WHERE
        matchEntity.player1 <> :playerName AND
        matchEntity.player2 = null AND
        matchEntity.isReady = false AND
        matchEntity.isCompleted = false
    """,
    )
    fun findAvailableMatches(@Param("playerName") playerName: String, page: Pageable): Page<MatchEntity>

    @Query(
        value = """
        SELECT matchEntity
    FROM MatchEntity matchEntity
    WHERE
        ( matchEntity.player1 = :playerName OR matchEntity.player2 = :playerName ) AND
        matchEntity.isCompleted = TRUE
        """,
    )
    fun findCompletedMatches(playerName: String, page: Pageable): Page<MatchEntity>

    @Query(
        """
        SELECT matchEntity
        FROM MatchEntity matchEntity
        WHERE
        matchEntity.player2 = null AND
        matchEntity.isReady = false AND
        matchEntity.isCompleted = false
    """,
    )
    fun findOpenMatch(
        page: PageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "createdDate")),
    ): Page<MatchEntity>

    @Query(
        """
        SELECT matchEntity
        FROM MatchEntity matchEntity
        WHERE
        matchEntity.player1 != :player AND
        (matchEntity.matchRank between (:rank - :range) and (:rank + :range)) and
        matchEntity.player2 = null AND
        matchEntity.isReady = false AND
        matchEntity.isCompleted = false AND
        matchEntity.matchMakingCount < ${Constants.MAX_MATCH_ATTEMPTS}
    """,
    )
    fun findOpenMatchInRange(
        player: String,
        rank: Int,
        range: Int,
        page: PageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "createdDate")),
    ): Page<MatchEntity>

    @Query(
        """
        SELECT matchEntity
        FROM MatchEntity matchEntity
        WHERE
        matchEntity.player1 != :player AND
        matchEntity.player2 = null AND
        matchEntity.isReady = false AND
        matchEntity.isCompleted = false and
        matchEntity.matchMakingCount < ${Constants.MAX_MATCH_ATTEMPTS}
    """,
    )
    fun findOpenMatch(
        player: String,
        page: PageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "lastModifiedDate")),
    ): Page<MatchEntity>

    @Query(
        """
        SELECT matchEntity
        FROM MatchEntity matchEntity
        WHERE
        matchEntity.matchMakingCount < ${Constants.MAX_MATCH_ATTEMPTS}
    """,
    )
    fun findMaxAttemptMatches(
        page: Pageable = PageRequest.of(
            0,
            100,
            Sort.by(Sort.Direction.DESC, "lastModifiedDate"),
        ),
    ): Page<MatchEntity>
}
