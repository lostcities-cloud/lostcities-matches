package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.concerns.users.UserService
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController {
    @Autowired
    lateinit var userService: UserService
    @Autowired
    lateinit var modelMapper : ModelMapper

    @GetMapping("/{id}")
    fun findUserById(@PathVariable  id: Long) : UserDto? {
        return userService.findById(id)
            .map { modelMapper.map(it, UserDto::class.java) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }
}