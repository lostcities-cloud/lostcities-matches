package io.dereknelson.lostcities.api.matches

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
    fun findById(@PathVariable id: Long) : GameState? {
        val match = matchService.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        return gameService.constructStateFromMatch(match)
    }
}
