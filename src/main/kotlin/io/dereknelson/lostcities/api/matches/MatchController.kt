package io.dereknelson.lostcities.api.matches

import io.dereknelson.lostcities.domains.matches.Match
import io.dereknelson.lostcities.domains.matches.MatchService
import io.dereknelson.lostcities.domains.matches.UserPair
import io.dereknelson.lostcities.domains.user.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.server.ResponseStatusException
import springfox.documentation.annotations.ApiIgnore

@Api("Matches and match making")
@RestController
@RequestMapping("/api/matches", produces=[MediaType.APPLICATION_JSON_VALUE])
class MatchController (
    private var userService: UserService,
    private var matchService: MatchService,
    private var modelMapper : ModelMapper
) {

    @ApiOperation(value = "Create and join a new match.")
    @ApiResponses(value = [
        ApiResponse(code=201, message="Match created."),
    ])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAndJoin(
        @AuthenticationPrincipal @ApiIgnore userDetails: UserDetails
    ): Match {
        val matchDto = userService.find(userDetails)
            .map { MatchDto(players=UserPair(user1=it)) }
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED) }

        return matchService
            .create(modelMapper.map(matchDto, Match::class.java))
    }

    @ApiOperation(value = "Join an existing match.")
    @ApiResponses(value = [
        ApiResponse(code=200, message="Match joined."),
        ApiResponse(code=409, message="This match is already started.")
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

    @ApiOperation(value = "Find an existing match.")
    @ApiResponses(value = [
        ApiResponse(code=200, message="Match found."),
        ApiResponse(code=404, message="Match not found.")
    ])
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : MatchDto {
        return matchService.findById(id)
            .map { modelMapper.map(it, MatchDto::class.java) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @ApiOperation(value = "Find matches available to join.")
    @GetMapping
    fun findAvailableForUser(@AuthenticationPrincipal userDetails : UserDetails): List<MatchDto>  {
        TODO("Not Implemented")
    }
}
