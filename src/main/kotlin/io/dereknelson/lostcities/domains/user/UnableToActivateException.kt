package io.dereknelson.lostcities.domains.user

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unable to activate key.")
class UnableToActivateException(message: String): RuntimeException(message)