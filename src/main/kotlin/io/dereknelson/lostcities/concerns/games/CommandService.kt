package io.dereknelson.lostcities.concerns.games

import org.springframework.stereotype.Service

@Service
class CommandService {

    fun applyCommand(game: GameState, command: Command) {
        if(game.currentPlayer.id == command.playerId && command.phase == game.phase && command.validate()) {
            execute(game, command)
        }
    }

    fun applyCommands(game: GameState, commands: List<Command>) {
        commands.forEach { applyCommand(game, it) }
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
            game.discard(command.playerId, command.card!!)
        } else {
            game.playCard(command.playerId, command.card!!)
        }

        game.nextPhase()
    }

    private fun draw(game: GameState, command: Command) {
        if(command.color != null) {
            game.drawFromDiscard(command.playerId, command.color)
        } else {
            game.drawCard(command.playerId)
        }

        game.nextPhase()
    }
}