package io.dereknelson.lostcities.common

data class User(
    val id: Long?,
    val login: String,
    val email: String,
    val langKey: String = Constants.DEFAULT_LANGUAGE
)
