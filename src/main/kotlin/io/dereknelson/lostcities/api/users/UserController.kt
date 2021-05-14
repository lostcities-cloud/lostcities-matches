package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.common.User
import io.dereknelson.lostcities.concerns.user.Registration
import io.dereknelson.lostcities.concerns.user.UserService
import io.dereknelson.lostcities.concerns.user.entity.AuthorityEntity
import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.dereknelson.lostcities.library.security.jwt.JwtFilter
import io.dereknelson.lostcities.library.security.jwt.TokenProvider
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.modelmapper.ModelMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Api("User actions")
@RestController
@RequestMapping("/api")
class UserController {
    private val log: Logger = LoggerFactory.getLogger(UserController::class.java)

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Autowired
    private lateinit var authenticationManagerBuilder: AuthenticationManagerBuilder

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var modelMapper : ModelMapper

    @ApiOperation(value = "Register a new user.")
    @ApiResponses(value = [
        ApiResponse(code=200, message=""),
        ApiResponse(code=409, message="User already exists.")
    ])
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody registrationDto : RegistrationDto): UserDto? {
        val registration = modelMapper.map(registrationDto, Registration::class.java)
        registration.authorities = setOf(AuthorityEntity(name= AuthoritiesConstants.USER))
        val user = userService.register(registration)

        return UserDto(user.id, user.login, user.email, user.langKey)
    }

    @ApiOperation(value = "Find a user.")
    @GetMapping("/user/{id}")
    fun findUserById(@PathVariable  id: Long) : UserDto? {
        return userService.findById(id)
            .map { UserDto(id=it.id, login=it.login, email=it.email, langKey=it.langKey) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @GetMapping("/authenticate")
    fun isAuthenticated(request: HttpServletRequest): String? {
        log.debug("REST request to check if the current user is authenticated")
        return request.remoteUser
    }

    @PostMapping("/authenticate")
    fun authorize(@Valid @RequestBody loginDto: LoginDto): ResponseEntity<JwtTokenDto> {
        val authenticationToken = UsernamePasswordAuthenticationToken(
            loginDto.username,
            loginDto.password
        )

        val authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = tokenProvider.createToken(authentication, loginDto.rememberMe)
        val httpHeaders = HttpHeaders()
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer $jwt")
        return ResponseEntity<JwtTokenDto>(JwtTokenDto(jwt), httpHeaders, HttpStatus.OK)
    }

    @GetMapping("/activate")
    fun activateAccount(@RequestParam(value = "key", required=true) key: String) {
        val user: Optional<User> = userService.activateRegistration(key)
        user.orElseThrow { InvalidActivationKeyException() }
    }

}

