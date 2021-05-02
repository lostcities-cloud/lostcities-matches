package io.dereknelson.lostcities.api.register

import io.dereknelson.lostcities.concerns.users.Registration
import io.dereknelson.lostcities.concerns.users.User
import io.dereknelson.lostcities.concerns.users.UserService
import io.dereknelson.lostcities.concerns.users.entity.AuthorityEntity
import io.dereknelson.lostcities.library.security.AuthoritiesConstants
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/registration")
class RegistrationController {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var modelMapper : ModelMapper

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody registrationDto : RegistrationDto): User? {
        val registration = modelMapper.map(registrationDto, Registration::class.java)
        registration.authorities = setOf(AuthorityEntity(name= AuthoritiesConstants.USER))
        return userService.register(registration)
    }
}