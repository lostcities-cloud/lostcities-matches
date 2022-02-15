package io.dereknelson.lostcities.matches.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import io.dereknelson.lostcities.models.matches.TurnChangeEvent
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class MatchEventService(
    val objectMapper: ObjectMapper,
    val matchRepository: MatchRepository
) {

    companion object {
        const val TURN_CHANGE_EVENT = "turn-change-event"
    }

    @Bean
    fun createGame(): Queue {
        return Queue(TURN_CHANGE_EVENT )
    }

    @RabbitListener(queues = [TURN_CHANGE_EVENT])
    fun createGame(gameMessage: Message) {
        val turnChangeEvent = objectMapper.readValue(gameMessage.body, TurnChangeEvent::class.java)

        val match = matchRepository.findById(turnChangeEvent.matchId)

        match.ifPresent {
            it.currentPlayer = turnChangeEvent.nextPlayer
            matchRepository.save(it)
        }
    }
}