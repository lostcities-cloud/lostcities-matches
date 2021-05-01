package io.dereknelson.lostcities.api.matches

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
class MatchController {
    @Autowired
    lateinit var matchService: MatchService

    @Autowired
    lateinit var modelMapper : ModelMapper

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody matchDto : MatchDto): Match {
        return matchService
            .create(modelMapper.map(matchDto, Match::class.java))
    }

    @GetMapping("{id}")
    fun findById(@PathVariable id: Long) : MatchDto? {
        return matchService
            .findById(id)
            .map { modelMapper.map(it, MatchDto::class.java) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @GetMapping
    fun findAvailableForUser(@AuthenticationPrincipal userDetails : UserDetails): String  {
        //println(userDetails.userId)

        return "asdf"
    }
}
