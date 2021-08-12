package io.dereknelson.lostcities.matches.api

import io.dereknelson.lostcities.common.model.match.Match
import io.dereknelson.lostcities.matches.service.MatchService
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.domains.user.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/api/matches", produces=[MediaType.APPLICATION_JSON_VALUE])
class MatchController (
    private var userService: UserService,
    private var matchService: MatchService,
    private var modelMapper : ModelMapper
) {

    @Operation(description = "Create and join a new match.")
    @ApiResponses(value = [
        ApiResponse(responseCode="201", description="Match created."),
    ])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAndJoin(
        @AuthenticationPrincipal @Parameter(hidden=true) userDetails: UserDetails
    ): Match {
        val matchDto = userService.find(userDetails)
            .map { MatchDto(players= UserPair(user1=it)) }
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED) }

        return matchService
            .create(modelMapper.map(matchDto, Match::class.java))
    }

    @Operation(description = "Join an existing match.")
    @ApiResponses(value = [
        ApiResponse(responseCode="200", description="Match joined."),
        ApiResponse(responseCode="409", description="This match is already started.")
    ])
    @PatchMapping("/{id}")
    fun joinMatch(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ) {
        val match = matchService.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        val user = userService.find(userDetails)
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED) }

        matchService.joinMatch(match, user)
    }

    @Operation(description = "Find an existing match.")
    @ApiResponses(value = [
        ApiResponse(responseCode="200", description="Match found."),
        ApiResponse(responseCode="404", description="Match not found.")
    ])
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : MatchDto {
        return matchService.findById(id)
            .map { modelMapper.map(it, MatchDto::class.java) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @Operation(description = "Find matches available to join.")
    @GetMapping
    fun findAvailableForUser(@AuthenticationPrincipal userDetails : UserDetails): List<MatchDto>  {
        TODO("Not Implemented")
    }
}
