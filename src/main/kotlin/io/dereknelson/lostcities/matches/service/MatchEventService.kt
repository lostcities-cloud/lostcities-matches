package io.dereknelson.lostcities.matches.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.ConnectionFactory
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import io.dereknelson.lostcities.models.matches.TurnChangeEvent
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component


@Component @Lazy(false)
class MatchEventService(
    val objectMapper: ObjectMapper,
    val matchRepository: MatchRepository
) {

    companion object {
        const val TURN_CHANGE_EVENT = "turn-change-event"
    }

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
}