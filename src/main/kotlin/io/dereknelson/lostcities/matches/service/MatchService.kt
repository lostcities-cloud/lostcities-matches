package io.dereknelson.lostcities.matches.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.matches.persistence.MatchEntity
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class MatchService(
    private var rabbitTemplate: RabbitTemplate,
    private var matchRepository: MatchRepository,
    private var objectMapper: ObjectMapper
) {

    companion object {
        const val CREATE_GAME_QUEUE = "create-game"
        const val CREATE_GAME_QUEUE_DLQ = "create-game-dlq"
    }

    @Bean @Qualifier(CREATE_GAME_QUEUE)
    fun createGame() = QueueBuilder
        .durable(CREATE_GAME_QUEUE)
        .ttl(5000)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", CREATE_GAME_QUEUE_DLQ)
        .build()!!

    @Bean @Qualifier(CREATE_GAME_QUEUE_DLQ)
    fun createGameDlQueue() = QueueBuilder
        .durable(CREATE_GAME_QUEUE_DLQ)
        .build()!!

    private val random: Random = Random()

    fun findById(id: Long) = matchRepository.findById(id)

    fun findActiveMatches(player: String, page: Pageable) =
        matchRepository.findActiveMatches(player, page)


    fun findCompletedMatches(player: String, page: Pageable): Page<MatchEntity> =
        matchRepository.findCompletedMatches(player, page)


    fun findAvailableMatches(player: String, page: Pageable) =
        matchRepository.findAvailableMatches(player, page)


    fun create(match: MatchEntity) =
        match.let {
            it.seed = random.nextLong()
            matchRepository.save(match)
        }

    fun joinMatch(match: MatchEntity, user: String): MatchEntity {
        if (match.hasPlayer(user) || match.player2 != null) {
            throw UnableToJoinMatchException()
        }

        match.player2 = user
        match.isReady = true

        val savedMatch = matchRepository.save(match)

        rabbitTemplate.convertAndSend(CREATE_GAME_QUEUE, objectMapper.writeValueAsString(savedMatch))

        return savedMatch
    }

    fun concede(matchEntity: MatchEntity, user: String): MatchEntity {
        if(!matchEntity.hasPlayer(user) || matchEntity.concededBy != null) {
            throw RuntimeException("Unable to concede match [${matchEntity.id}]")
        }

        matchEntity.isCompleted = true
        matchEntity.concededBy = user
        return matchRepository.save(matchEntity)
    }

    fun finishGame(id: Long, finishedAt: LocalDateTime, scores: Map<String, Int>) {
        val match = matchRepository.findById(id)
            .orElseThrow { RuntimeException("Unable to find match for completion: $id") }

        if(scores.size != 2) {
            throw RuntimeException("Unable to find match for completion: $id")
        } else if(!(scores.containsKey(match.player1) && scores.containsKey(match.player2))) {
            throw RuntimeException("Incorrect players: $id")
        }

        match.isCompleted = true
        match.finishedAt = finishedAt

        scores.forEach { (player, score) ->
            updatePlayerScore(match, player, score)
        }

        matchRepository.save(match)
    }

    private fun updatePlayerScore(match: MatchEntity, player: String, score: Int) {
        println("Finish game: $player: $score")
        if(match.player1 === player) {
            match.score1 = score
        } else {
            match.score2 = score
        }
    }
}
