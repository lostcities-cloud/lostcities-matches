package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.common.User
import io.dereknelson.lostcities.domains.user.Registration
import io.dereknelson.lostcities.domains.user.UserService
import io.dereknelson.lostcities.domains.user.entity.AuthorityEntity
import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.dereknelson.lostcities.library.security.jwt.JwtFilter
import io.dereknelson.lostcities.library.security.jwt.TokenProvider


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

import org.modelmapper.ModelMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

@Tag(name = "User actions")
@RestController
@RequestMapping("/api")
class UserController (
    private var tokenProvider: TokenProvider,
    private var authenticationManagerBuilder: AuthenticationManagerBuilder,
    private var userService: UserService,
    private var modelMapper : ModelMapper
) {

    private val log: Logger = LoggerFactory.getLogger(UserController::class.java)

    @Operation(summary = "Register a new user.")
    @ApiResponses(value = [
        ApiResponse(responseCode="200", description=""),
        ApiResponse(responseCode="409", description="User already exists.")
    ])
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody registrationDto : RegistrationDto): UserDto? {
        val registration = modelMapper.map(registrationDto, Registration::class.java)
        registration.authorities = setOf(AuthorityEntity(name= AuthoritiesConstants.USER))
        val user = userService.register(registration)

        return UserDto(user.id, user.login, user.email, user.langKey)
    }

    @Operation(summary = "Find a user.")
    @GetMapping("/user/{id}")
    fun findUserById(@PathVariable  id: Long) : UserDto? {
        return userService.findById(id)
            .map { UserDto(id=it.id, login=it.login, email=it.email, langKey=it.langKey) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @Operation(summary = "Get my current user information.")
    @GetMapping("/authenticate")
    fun isAuthenticated(request: HttpServletRequest): String? {
        log.debug("REST request to check if the current user is authenticated")
        return request.remoteUser
    }

    @Operation(summary = "Authenticate a user.")
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

    @Operation(summary = "Activate a user.")
    @GetMapping("/activate")
    fun activateAccount(@RequestParam(value = "key", required=true) key: String) {
        val user: Optional<User> = userService.activateRegistration(key)
        user.orElseThrow { InvalidActivationKeyException() }
    }

}

