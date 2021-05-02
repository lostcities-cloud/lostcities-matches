package io.dereknelson.lostcities.api.users

data class UserDto(
    var id: Long?=null,
    val login: String?=null,
    val email: String?=null,
    val langKey: String="en_US"
)