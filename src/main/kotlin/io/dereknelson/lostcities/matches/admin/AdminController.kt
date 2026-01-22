package io.dereknelson.lostcities.matches.admin

import io.dereknelson.lostcities.common.Constants.AI_USER_NAMES
import io.dereknelson.lostcities.common.auth.LostCitiesUserDetails
import io.dereknelson.lostcities.matches.match.MatchService
import io.dereknelson.lostcities.matches.match.AiPlayer
import io.dereknelson.lostcities.matches.match.MatchEntity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.Random

@RestController()
@CrossOrigin(
    "http://localhost:8080",
    "http://192.168.1.241:8080",
    "http://192.168.1.231:8091",
    "http://192.168.1.233:80",
    "*",
)
@RequestMapping("/matches/admin")
class AdminController(
    private var matchService: MatchService,
) {
    private var random: Random = Random()

    @Operation(
        description = "Create bulk ai matches.",
        security = [ SecurityRequirement(name = "bearer-key") ],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Match created."),
        ],
    )
    @PostMapping("ai/matches", "ai/matches/")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun createAndJoin(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
        @RequestBody aiPlayer: AiPlayer = AiPlayer(true, count = 1000),
    ) {
        while (aiPlayer.count-- > 0) {
            val matchEntity = MatchEntity.buildMatch(player = AI_USER_NAMES.random(), random.nextLong())
            matchEntity.isPlayer1Ai = true

            val match = matchService.create(matchEntity)

            val player2 = AI_USER_NAMES.filter { it != matchEntity.player1 }.random()
            matchService.joinMatch(match, player2, isAiPlayer = true)
        }
    }
}
