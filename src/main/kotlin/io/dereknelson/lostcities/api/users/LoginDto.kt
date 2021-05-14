package io.dereknelson.lostcities.api.users

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class LoginDto {
    val username: @NotNull @Size(min = 1, max = 50) String? = null

    val password: @NotNull @Size(min = 4, max = 100) String? = null

    val rememberMe = false
}