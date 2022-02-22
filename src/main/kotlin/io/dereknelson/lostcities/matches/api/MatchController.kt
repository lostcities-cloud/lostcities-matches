package io.dereknelson.lostcities.matches.api

import io.dereknelson.lostcities.common.auth.LostCitiesUserDetails
import io.dereknelson.lostcities.matches.service.Match
import io.dereknelson.lostcities.matches.service.MatchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/matches", produces = [MediaType.APPLICATION_JSON_VALUE])
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
    // @PreAuthorize("hasAuthority('ROLE_USER')")
    fun createAndJoin(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails,
        @RequestParam("ai") ai: Boolean = false
    ): MatchDto {
        if (ai) {
            throw ResponseStatusException(HttpStatus.NOT_IMPLEMENTED)
        }

        val match = matchService.create(
            Match.buildMatch(player = userDetails.login, random)
        )

        return match.asMatchDto()
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
    fun findById(@PathVariable id: Long): MatchDto {
        return matchService.findById(id)
            .map { it.asMatchDto() }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @Operation(
        description = "Find matches available to join.",
        security = [ SecurityRequirement(name = "bearer-key") ]
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun findAvailableForUser(
        @AuthenticationPrincipal @Parameter(hidden = true) userDetails: LostCitiesUserDetails
    ): List<MatchDto> {
        return matchService.getMatches()
            .map { it.asMatchDto() }
    }

    private fun Match.asMatchDto(): MatchDto {
        return MatchDto(
            id,
            players,
            this.currentPlayer,
            isReady,
            isStarted,
            isCompleted
        )
    }
}
