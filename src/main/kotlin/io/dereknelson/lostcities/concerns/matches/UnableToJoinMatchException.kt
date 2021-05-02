package io.dereknelson.lostcities.concerns.matches

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User was unable to join the requested match.")
class UnableToJoinMatchException : RuntimeException()
