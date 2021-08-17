package io.dereknelson.lostcities.matches.service

import io.dereknelson.lostcities.common.model.match.Match
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.matches.persistence.MatchEntity
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class MatchService(
    private var matchRepository : MatchRepository
) {

    private val random : Random = Random()

    fun markStarted(match: Match): Match {
        var matchEntity = match.toMatchEntity()

        if (matchEntity.isReady && !matchEntity.isStarted) {
            matchEntity.isStarted = true
            matchEntity = matchRepository.save(matchEntity)

            return matchEntity.toMatch()
        } else {
            throw RuntimeException("Unable to start match [${match.id}]")
        }
    }

    fun markCompleted(match: Match): Match {
        var matchEntity = match.toMatchEntity()

        if (
            !matchEntity.isStarted ||
            !matchEntity.isReady ||
            matchEntity.isCompleted
        ) {
            throw RuntimeException("Unable to complete match [${match.id}]")
        } else {
            matchEntity.isCompleted = true
            matchEntity = matchRepository.save(matchEntity)

            return matchEntity.toMatch()
        }
    }

    fun concede(match: Match, userId: Long): Match {
        var matchEntity = match.toMatchEntity()

        if (
            matchEntity.concededBy != null || match.players.contains(userId)
        ) {
            throw RuntimeException("Unable to complete match [${match.id}]")
        } else {
            matchEntity.isCompleted = true
            matchEntity.concededBy = userId
            matchEntity = matchRepository.save(matchEntity)

            return matchEntity.toMatch()
        }
    }

    fun create(match: Match): Match {
        val matchEntity = match.toMatchEntity()
        matchEntity.seed = random.nextLong()
        return matchRepository.save(matchEntity).toMatch()
    }

    fun findById(id: Long): Optional<Match> {
        return matchRepository.findById(id).map { it.toMatch() }
    }

    fun deleteById(id: Long) {
        matchRepository.deleteById(id)

    }

    fun joinMatch(match: Match, userId: Long): Match {
        if(match.players.contains(userId) || match.players.isPopulated) {
            throw UnableToJoinMatchException()
        }

        val matchEntity = match.toMatchEntity()

        matchEntity.player2 = userId
        matchEntity.isReady = true

        return matchRepository.save(matchEntity).toMatch()
    }

    private fun MatchEntity.toMatch(): Match {
        return Match(
            id = this.id,
            seed = this.seed,
            players = UserPair(
                user1 = this.player1,
                user2 = this.player2,
                score1 = this.score1,
                score2 = this.score2
            ),
            concededBy = this.concededBy,
            isReady = this.isReady,
            isStarted = this.isStarted,
            isCompleted = this.isCompleted,
            createdDate = LocalDateTime.ofInstant(this.createdDate, ZoneOffset.UTC),
            lastModifiedDate = LocalDateTime.ofInstant(this.lastModifiedDate, ZoneOffset.UTC),
            createdBy = this.createdBy,
        )
    }

    private fun Match.toMatchEntity(): MatchEntity {
        return MatchEntity(
            id=this.id,
            seed=this.seed,
            player1=this.players.user1,
            player2=this.players.user2,
            score1=this.players.score1,
            score2 = this.players.score2,
            isReady = this.isReady,
            isStarted = this.isStarted,
            isCompleted = this.isCompleted,
            concededBy = this.concededBy
        )
    }
}