package io.dereknelson.lostcities.matches.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.dereknelson.lostcities.matches.persistence.MatchEntity
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import io.dereknelson.lostcities.models.matches.FinishMatchEvent
import io.dereknelson.lostcities.models.matches.TurnChangeEvent
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class MatchEventAmqpListener(
    val objectMapper: ObjectMapper,
    val matchRepository: MatchRepository,
    val matchService: MatchService
) {

    companion object {
        const val TURN_CHANGE_EVENT = "turn-change"
        const val TURN_CHANGE_EVENT_DLQ = "turn-change-dlq"
        const val END_GAME_EVENT = "end-game"
        const val END_GAME_EVENT_DLQ = "end-game-dlq"
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

    @Bean @Qualifier(TURN_CHANGE_EVENT)
    fun turnChangeEventQueue() = QueueBuilder
        .durable(TURN_CHANGE_EVENT)
        .ttl(5000)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", TURN_CHANGE_EVENT_DLQ)
        .build()!!

    @Bean @Qualifier(TURN_CHANGE_EVENT_DLQ)
    fun turnChangeEventDLQueue() = QueueBuilder
        .durable(TURN_CHANGE_EVENT_DLQ)
        .build()!!

    @Bean @Qualifier(END_GAME_EVENT)
    fun endGameEventQueue() = QueueBuilder
        .durable(END_GAME_EVENT)
        .ttl(5000)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", END_GAME_EVENT_DLQ)
        .build()!!

    @Bean @Qualifier(END_GAME_EVENT_DLQ)
    fun endGameEventDLQueue() = QueueBuilder
        .durable(END_GAME_EVENT_DLQ)
        .build()!!

    @RabbitListener(queues = [TURN_CHANGE_EVENT], exclusive = true)
    fun gameEvent(gameMessage: Message) {
        val turnChangeEvent = objectMapper.readValue(gameMessage.body, TurnChangeEvent::class.java)

        println(turnChangeEvent.toString())

        val match = matchRepository.findById(turnChangeEvent.matchId)

        match.ifPresent {
            it.currentPlayer = turnChangeEvent.nextPlayer
            matchRepository.save(it)
        }
    }

    @RabbitListener(queues = [END_GAME_EVENT], exclusive = true)
    fun endMatchEvent(matchEvent: Message) {
        try {
            val finishMatch = objectMapper.readValue<FinishMatchEvent>(matchEvent.body)

            println("Finished: $finishMatch")

            matchService.finishGame(finishMatch.id, finishMatch.finishedAt, finishMatch.scores)
        } catch (e: RuntimeException) {
            throw ListenerException()
        }
    }
}

@Service
class MatchEventAmqpService(
    val objectMapper: ObjectMapper,
    private var rabbitTemplate: RabbitTemplate,
) {
    fun convertAndSend(topic: String, match: MatchEntity) {
        val jsonMatch = objectMapper.writeValueAsString(match)
        rabbitTemplate.convertAndSend(topic, jsonMatch)
    }
}
class ListenerException : Exception()
