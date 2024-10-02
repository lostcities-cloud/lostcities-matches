package io.dereknelson.lostcities.matches.api

import io.dereknelson.lostcities.common.auth.LostCitiesUserDetails
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.matches.persistence.MatchEntity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.annotation.security.RolesAllowed
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController("/matches")
class MatchController(
    private var matchService: MatchService
) {
    private var random: Random = Random()

    @Operation(
        description = "Create and join a new match.",
        security = [ SecurityRequirement(name = "bearer-key") ]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Match created."),
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAndJoin(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
        @RequestParam("ai") ai: Boolean = false
    ): MatchDto {
        if (ai) {
            throw ResponseStatusException(HttpStatus.NOT_IMPLEMENTED)
        }

        return matchService.create(
            MatchEntity.buildMatch(player = userDetails.login, random.nextLong())
        ).asMatchDto()
    }

    @Operation(
        description = "Join an existing match.",
        security = [ SecurityRequirement(name = "bearer-key") ]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Match joined."),
            ApiResponse(responseCode = "409", description = "This match is already started.")
        ]
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun joinMatch(
        @PathVariable id: Long,
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails
    ) {
        val match = matchService.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        matchService.joinMatch(match, userDetails.login)
    }

    @Operation(
        description = "Find an existing match.",
        security = [ SecurityRequirement(name = "bearer-key") ]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Match found."),
            ApiResponse(responseCode = "404", description = "Match not found.")
        ]
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun findById(@PathVariable id: Long) =
        matchService.findById(id)
            .map { it.asMatchDto() }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @Operation(
        description = "Find matches available to join.",
        security = [ SecurityRequirement(name = "bearer-key") ]
    )
    @GetMapping("/available")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun findAvailableForUser(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
        @PageableDefault(page = 0, size = 10) page: PageRequest
    ) = matchService.findAvailableMatches(userDetails.login, page)
        .map { it.asMatchDto() }

    @Operation(
        description = "Find active matches for player.",
        security = [ SecurityRequirement(name = "bearer-key") ]
    )
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun findActiveMatches(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
        @PageableDefault(page = 0, size = 10) page: PageRequest
    ): Page<MatchDto> {
        val x = matchService.findActiveMatches(userDetails.login, page)

        return x.map { it.asMatchDto() }
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
                score2 = score2
            ),
            this.currentPlayer,
            isReady,
            isStarted,
            isCompleted
        )
    }
}
