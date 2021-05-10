package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.concerns.users.Registration
import io.dereknelson.lostcities.concerns.users.User
import io.dereknelson.lostcities.concerns.users.UserService
import io.dereknelson.lostcities.concerns.users.entity.AuthorityEntity
import io.dereknelson.lostcities.library.security.AuthoritiesConstants
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Api("Registration")
@RestController
@RequestMapping("/api/registration")
class RegistrationController {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var modelMapper : ModelMapper

    @ApiOperation(value = "Register a new user.")
    @ApiResponses(value = [
        ApiResponse(code=200, message=""),
        ApiResponse(code=409, message="User already exists.")
    ])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody registrationDto : RegistrationDto): UserDto? {
        val registration = modelMapper.map(registrationDto, Registration::class.java)
        registration.authorities = setOf(AuthorityEntity(name= AuthoritiesConstants.USER))

        val user = userService.register(registration)

        return UserDto(user.id, user.login, user.email, user.langKey)
    }
}