package io.dereknelson.lostcities.commandJobProcessor

import io.dereknelson.lostcities.api.game.MatchAggregate
import io.dereknelson.lostcities.config.KafkaConfiguration
import io.dereknelson.lostcities.commandJobProcessor.game.CommandService
import io.dereknelson.lostcities.commandJobProcessor.game.GameService
import io.dereknelson.lostcities.common.model.game.GameState
import io.dereknelson.lostcities.common.model.match.Match
import org.springframework.http.HttpStatus
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class CommandJobProccessor(
    val gameService: GameService,
    val commandService: CommandService,
    val kafkaConfiguration: KafkaConfiguration
) {

    lateinit var kafkaTemplate: KafkaTemplate<String, GameState>

    @KafkaListener(topics = ["command-job-topic"])
    fun commandJob(matchAggregate: MatchAggregate) {
        try {
            val gameState = constructGameState(matchAggregate.match)

            commandService.applyCommand(gameState, matchAggregate.command)
            commandService.save(matchAggregate.command)

            kafkaTemplate.send(kafkaConfiguration.gameChangeNotificationTopic, gameState)
        } catch (e: RuntimeException) {
            throw ResponseStatusException(HttpStatus.NOT_ACCEPTABLE)
        }
    }

    private fun constructGameState(match: Match): GameState {
        val gameState = gameService.constructStateFromMatch(match)
        commandService.applyCommands(gameState)
        return gameState
    }
}