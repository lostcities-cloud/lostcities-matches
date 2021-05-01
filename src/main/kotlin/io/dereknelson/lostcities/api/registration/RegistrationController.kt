package io.dereknelson.lostcities.api.registration

import io.dereknelson.lostcities.api.users.UserDto
import io.dereknelson.lostcities.concerns.users.Registration
import io.dereknelson.lostcities.concerns.users.UserService
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/registration")
class RegistrationController {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var modelMapper : ModelMapper

    @PostMapping
    fun register(registrationDto : RegistrationDto) {
        return userService.register(modelMapper.map(registrationDto, Registration::class.java))
    }
}