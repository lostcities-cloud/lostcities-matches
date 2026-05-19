package io.dereknelson.lostcities.matches.match

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.dereknelson.lostcities.matches.END_GAME_EVENT
import io.dereknelson.lostcities.matches.END_GAME_EVENT_DLQ
import io.dereknelson.lostcities.matches.FinishGameScore
import io.dereknelson.lostcities.matches.GAME_EVENT_QUEUE
import io.dereknelson.lostcities.matches.TURN_CHANGE_EVENT
import io.dereknelson.lostcities.matches.TURN_CHANGE_EVENT_DLQ
import io.dereknelson.lostcities.models.gamestate.GameEvent
import io.dereknelson.lostcities.models.matches.GameDto
import io.dereknelson.lostcities.models.matches.events.FinishMatchEvent
import io.dereknelson.lostcities.models.matches.events.TurnChangeEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.annotation.Argument
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class MatchEventAmqpListener(
    val events: ApplicationEventPublisher,
    val objectMapper: ObjectMapper,
    val matchRepository: MatchRepository,
    val matchService: MatchService,
) {
    val logger: Logger = LoggerFactory.getLogger(MatchEventAmqpListener::class.java)


    //@RabbitListener(queues = [TURN_CHANGE_EVENT], exclusive = false, concurrency = "1-8")
    @RabbitListener(bindings =
        [
            QueueBinding(
                value = Queue(
                    name="\${app.turnChangeEventQueue}",
                    durable =  "true",
                    exclusive = "false",
                    autoDelete = "false",
                    declare = "true",
                    arguments = [
                        Argument("x-dead-letter-exchange", ""),
                        Argument("x-dead-letter-routing-key", TURN_CHANGE_EVENT_DLQ)
                    ]
                ),
                exchange = Exchange(
                    name = "game-events.matches",
                    durable = "true",
                    declare="false",
                    autoDelete = "false",
                    type = "direct"
                ),
                key = ["match-events.turn-change"]
            ),

        ])
        //, concurrency = "1-8")
    fun gameEvent(gameMessage: Message) {
        val turnChangeEvent = objectMapper.readValue(gameMessage.body, TurnChangeEvent::class.java)

        logger.debug("{} {}", gameMessage.messageProperties.consumerQueue, turnChangeEvent)

        val match = matchRepository.findById(turnChangeEvent.matchId)

        match.ifPresent {
            it.currentPlayer = turnChangeEvent.nextPlayer
            it.turns = turnChangeEvent.turns
            it.isStarted = true
            matchRepository.save(it)
        }
    }

    //@RabbitListener(queues = [END_GAME_EVENT], exclusive = true)
    @RabbitListener(bindings = [
        QueueBinding(
            value = Queue(
                name="\${app.endGameEventQueue}",
                durable =  "true",
                exclusive = "true",
                autoDelete = "false",
                declare = "true",
                arguments = [
                    Argument("x-dead-letter-exchange", ""),
                    Argument("x-dead-letter-routing-key", END_GAME_EVENT_DLQ)
                ]
            ),
            exchange = Exchange(
                name = "game-events.matches",
                declare="false",
            ),
            key = ["game-events.matches.end-game-event"]
        )
    ])
    fun endMatchEvent(matchEvent: Message) {
        try {
            val finishMatch = objectMapper.readValue<FinishMatchEvent>(matchEvent.body)

            var player1Name: String? = null
            var player1Score: Int? = null
            var player2Name: String? = null
            var player2Score: Int? = null

            finishMatch.scores.forEach { (id, score) ->
                if (player1Name === null) {
                    player1Name = id
                    player1Score = score
                } else {
                    player2Name = id
                    player2Score = score
                }
            }

            val scoreEvent = FinishGameScore(
                player1Name = player1Name!!,
                player1Score = player1Score!!,
                player2Name = player2Name!!,
                player2Score = player2Score!!,
            ).asEvent()

            events.publishEvent(scoreEvent)

            logger.info("Finished: $finishMatch")

            matchService.finishGame(finishMatch.id, finishMatch.finishedAt, finishMatch.scores)
        } catch (e: RuntimeException) {
            throw ListenerException()
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Service
class MatchEventAmqpService(
    private var rabbitTemplate: RabbitTemplate,
) {
    fun convertAndSend(gameDto: GameDto) {
        val gameEvent = GameEvent(id = gameDto.id, gameDto = gameDto)
        val matchProtobuf = ProtoBuf.encodeToByteArray(gameEvent)
        rabbitTemplate.convertAndSend("$GAME_EVENT_QUEUE.game-state-group", matchProtobuf)
    }
}
class ListenerException : Exception()
