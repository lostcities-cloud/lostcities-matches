package io.dereknelson.lostcities.api.matches

import io.dereknelson.lostcities.api.game.CommandDto
import io.dereknelson.lostcities.concerns.game.Command
import io.dereknelson.lostcities.concerns.game.CommandService
import io.dereknelson.lostcities.concerns.game.GameService
import io.dereknelson.lostcities.concerns.game.GameState
import io.dereknelson.lostcities.concerns.matches.MatchService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Api("Game")
@RestController
@RequestMapping("/api/game")
class GameController {

    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var commandService: CommandService

    @Autowired
    private lateinit var modelMapper: ModelMapper

    @ApiOperation(value = "Retrieve a game state.")
    @ApiResponses(value = [
        ApiResponse(code=200, message="Game retrieved."),
        ApiResponse(code=404, message="Game not found.")
    ])
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(
        @PathVariable id: Long,
        userDetails: UserDetails
    ): GameState? {
        return retrieveGame(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @ApiOperation(value = "Play a command in a game.")
    @ApiResponses(value = [
        ApiResponse(code=201, message="Command executed."),
        ApiResponse(code=400, message="Invalid command."),
        ApiResponse(code=404, message="Game not found."),
        ApiResponse(code=409, message="Unable to play command."),
    ])
    @PatchMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun playCommand(@PathVariable id: Long, @RequestBody commandDto: CommandDto) {
        val gameState = retrieveGame(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        val command = modelMapper.map(commandDto, Command::class.java)

        try {
            commandService.applyCommand(gameState, command)
            commandService.save(command)
        } catch (e: RuntimeException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun retrieveGame(id: Long): Optional<GameState> {
        return matchService.findById(id)
            .map { match -> gameService.constructStateFromMatch(match) }
            .map { game -> commandService.playAll(game) }
    }
}
