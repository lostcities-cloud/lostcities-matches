package io.dereknelson.lostcities.matches.exceptions

import java.net.URI

object ErrorConstants {
    const val ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure"
    const val ERR_VALIDATION = "error.validation"
    const val PROBLEM_BASE_URL = "https://www.jhipster.tech/problem"
    @JvmField
    val DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message")
    @JvmField
    val CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation")
    val INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password")
    val EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used")
    val LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used")
}