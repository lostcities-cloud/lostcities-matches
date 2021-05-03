package io.dereknelson.lostcities.concerns.game

import io.dereknelson.lostcities.concerns.game.components.Phase
import io.dereknelson.lostcities.concerns.game.entities.CommandEntity
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommandService {

    @Autowired
    private lateinit var commandRepository: CommandRepository

    @Autowired
    private lateinit var modelMapper: ModelMapper

    fun applyCommand(game: GameState, command: Command) {
        if(game.currentPlayer.login == command.player && command.phase == game.phase && command.validate()) {
            execute(game, command)
        } else {
            throw UnableToPlayCommandException()
        }
    }

    fun playAll(game: GameState) : GameState {
        commandRepository.findByMatchId(game.id)
            .map { modelMapper.map(it, Command::class.java) }
            .forEach { applyCommand(game, it) }

        return game
    }

    private fun execute(game: GameState, command: Command) {
        if(command.phase == Phase.PLAY_OR_DISCARD) {
            playOrDiscard(game, command)
        } else {
            draw(game, command)
        }
    }

    private fun playOrDiscard(game: GameState, command: Command) {
        if(command.discard) {
            game.discard(command.player, command.card!!)
        } else {
            game.playCard(command.player, command.card!!)
        }

        game.nextPhase()
    }

    private fun draw(game: GameState, command: Command) {
        if(command.color != null) {
            game.drawFromDiscard(command.player, command.color)
        } else {
            game.drawCard(command.player)
        }

        game.nextPhase()
    }

    fun save(command: Command?) {
        modelMapper.map(command, CommandEntity::class.java)
    }
}