package io.dereknelson.lostcities.api.matches

import io.dereknelson.lostcities.concerns.matches.Match
import io.dereknelson.lostcities.concerns.matches.MatchService
import io.dereknelson.lostcities.concerns.matches.UserPair
import io.dereknelson.lostcities.concerns.users.UserService
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/api/matches", produces=[MediaType.APPLICATION_JSON_VALUE])
class MatchController {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var matchService: MatchService

    @Autowired
    lateinit var modelMapper : ModelMapper

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAndJoin(
        @AuthenticationPrincipal @ApiIgnore userDetails: UserDetails
    ): Match {
        val user = userService.find(userDetails)
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED) }

        val matchDto = MatchDto(players=UserPair(user1=user))

        return matchService
            .create(modelMapper.map(matchDto, Match::class.java))
    }

    @ResponseStatus(HttpStatus.OK)
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

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : MatchDto? {
        return matchService
            .findById(id)
            .map { modelMapper.map(it, MatchDto::class.java) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @GetMapping
    fun findAvailableForUser(@AuthenticationPrincipal userDetails : UserDetails): List<MatchDto>  {
        return emptyList()
    }
}
