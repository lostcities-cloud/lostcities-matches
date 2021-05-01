package io.dereknelson.lostcities.concerns.users

data class User(
    val id: Long?,
    val login: String,
    val email: String,
    val langKey: String = "en_US"
)
