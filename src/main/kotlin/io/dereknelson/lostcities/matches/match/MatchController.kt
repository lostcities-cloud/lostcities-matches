package io.dereknelson.lostcities.matches.match

import io.dereknelson.lostcities.common.Constants.AI_USER_NAMES
import io.dereknelson.lostcities.common.auth.LostCitiesUserDetails
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.matches.MatchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController()
@CrossOrigin(
    "http://localhost:8080",
    "http://192.168.1.241:8080",
    "http://192.168.1.231:8091",
    "http://192.168.1.233:80",
    "*",
)
@RequestMapping("/matches")
class MatchController(
    private var matchService: MatchService,
) {
    private var random: Random = Random()

    @Operation(
        description = "Create and join a new match.",
        security = [ SecurityRequirement(name = "bearer-key") ],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Match created."),
        ],
    )
    @PostMapping("", "/")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun createAndJoin(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
        @RequestParam("isAi") ai: Boolean = false,
    ): MatchDto {
        val matchEntity = MatchEntity.buildMatch(player = userDetails.login, random.nextLong())

        val match = matchService.create(matchEntity)

        if (ai) {
            return matchService.joinMatch(match, AI_USER_NAMES.random(), true).asMatchDto()
        }

        return match.asMatchDto()
    }

    @Operation(
        description = "Join an existing match.",
        security = [ SecurityRequirement(name = "bearer-key") ],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Match joined."),
            ApiResponse(responseCode = "409", description = "This match is already started."),
        ],
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun joinMatch(
        @PathVariable id: Long,
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
    ) {
        val match = matchService.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        if (match.player2 == null) {
            throw ResponseStatusException(HttpStatus.NOT_MODIFIED)
        }

        matchService.joinMatch(match, userDetails.login)
    }

    @Operation(
        description = "Find an existing match.",
        security = [ SecurityRequirement(name = "bearer-key") ],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Match found."),
            ApiResponse(responseCode = "404", description = "Match not found."),
        ],
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun findById(@PathVariable id: Long) =
        matchService.findById(id)
            .map { it.asMatchDto() }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @Operation(
        description = "Find matches available to join.",
        security = [ SecurityRequirement(name = "bearer-key") ],
    )
    @GetMapping("/available")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun findAvailableForUser(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
        @PageableDefault(page = 0, size = 100) page: Pageable,
    ): Page<MatchDto> {
        return matchService.findAvailableMatches(userDetails.login, page)
            .map { it.asMatchDto() }
    }

    @Operation(
        description = "Find active matches for player.",
        security = [ SecurityRequirement(name = "bearer-key") ],
    )
    @GetMapping("/active")
    fun findActiveMatches(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
        @PageableDefault(page = 0, size = 500) page: Pageable,
    ): Page<MatchDto> {
        return matchService.findActiveMatches(userDetails.login, page)
            .map { it.asMatchDto() }
    }

    @GetMapping("/resend")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun resendMatchesToGamestate() {
        matchService.recreateMatches()
    }

    private fun MatchEntity.asMatchDto(): MatchDto {
        return MatchDto(
            id,
            UserPair(
                user1 = player1,
                user2 = player2,
                score1 = score1,
                score2 = score2,
            ),
            this.currentPlayer,
            isReady,
            isStarted,
            isCompleted,
        )
    }
}
