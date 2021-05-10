package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.concerns.users.User
import io.dereknelson.lostcities.concerns.users.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Api("User actions")
@RestController
@RequestMapping("/api/user")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var modelMapper : ModelMapper

    @ApiOperation(value = "Find a user.")
    @GetMapping("{id}")
    fun findUserById(@PathVariable  id: Long) : UserDto? {
        return userService.findById(id)
            .map { UserDto(id=it.id, login=it.login, email=it.email, langKey=it.langKey) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }
}