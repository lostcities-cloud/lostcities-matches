package io.dereknelson.lostcities.library.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User


class AuthenticatedUserDetails(
    val userId: Long,
    username: String?,
    password: String?,
    authorities: Collection<GrantedAuthority>?
) : User(username, password, authorities) {
}