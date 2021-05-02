package io.dereknelson.lostcities.api.register

import io.dereknelson.lostcities.library.Constants
import io.dereknelson.lostcities.library.security.AuthoritiesConstants
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class RegistrationDto (
    var login: @NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String,
    var email: @NotNull @Email @Size(min = 5, max = 254) String,
    var password: @NotNull @Size(min = 60, max = 60) String,
    var firstName: @NotNull @Size(max = 50) String,
    var lastName: @Size(max = 50) String?,
    var langKey: @Size(min = 2, max = 10) String = "en_US",
    var authorities: Set<String> = setOf(AuthoritiesConstants.USER),
)