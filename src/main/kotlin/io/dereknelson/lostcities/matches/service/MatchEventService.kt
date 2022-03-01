package io.dereknelson.lostcities.matches.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import io.dereknelson.lostcities.models.matches.FinishMatchEvent
import io.dereknelson.lostcities.models.matches.TurnChangeEvent
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component @Lazy(false)
class MatchEventService(
    val objectMapper: ObjectMapper,
    val matchRepository: MatchRepository,
    val matchService: MatchService
) {

    companion object {
        const val TURN_CHANGE_EVENT = "turn-change-event"
        const val END_GAME_EVENT = "end-game-event"
    }

    @Bean @Qualifier(TURN_CHANGE_EVENT)
    fun turnChangeEventQueue() = Queue(TURN_CHANGE_EVENT)

    @Bean @Qualifier(END_GAME_EVENT)
    fun endGameEventQueue() = Queue(END_GAME_EVENT)

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
        val finishMatch = objectMapper.readValue<FinishMatchEvent>(matchEvent.body)

        matchService.finishGame(finishMatch.id, finishMatch.finishedAt, finishMatch.scores)
    }
}
