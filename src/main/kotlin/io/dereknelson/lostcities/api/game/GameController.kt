package io.dereknelson.lostcities.api.matches

import io.dereknelson.lostcities.api.game.CommandDto
import io.dereknelson.lostcities.concerns.game.Command
import io.dereknelson.lostcities.concerns.game.CommandService
import io.dereknelson.lostcities.concerns.game.GameService
import io.dereknelson.lostcities.concerns.game.GameState
import io.dereknelson.lostcities.concerns.matches.Match
import io.dereknelson.lostcities.concerns.matches.MatchService
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/matches")
class GameController {

    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var commandService: CommandService

    @Autowired
    private lateinit var modelMapper : ModelMapper

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long,
        userDetails: UserDetails
    ) : GameState? {
        return retrieveGame(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @PatchMapping("/{id}")
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

    fun retrieveGame(id: Long): Optional<GameState> {
        return matchService.findById(id)
            .map { match -> gameService.constructStateFromMatch(match) }
            .map { game -> commandService.playAll(game)}
    }
}
