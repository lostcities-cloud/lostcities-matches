package io.dereknelson.lostcities.matches.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User was unable to join the requested match.")
class UnableToJoinMatchException : RuntimeException()
