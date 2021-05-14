package io.dereknelson.lostcities.concerns.user

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "User is not activated.")
class UserNotActivatedException(message: String?) : AuthenticationException(message)