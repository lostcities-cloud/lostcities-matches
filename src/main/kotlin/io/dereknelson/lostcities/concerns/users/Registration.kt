package io.dereknelson.lostcities.concerns.users

import io.dereknelson.lostcities.concerns.users.entity.AuthorityEntity
import io.dereknelson.lostcities.library.Constants
import org.apache.commons.lang3.StringUtils
import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class Registration (
    val login: String,
    val email: String,
    var password: @NotNull @Size(min = 60, max = 60) String,
    val firstName: @NotNull @Size(max = 50) String,
    val lastName: @Size(max = 50) String?,
    val langKey: @NotNull @Size(min = 2, max = 10) String = "en_US",
    var authorities: @Size(min = 1) Set<AuthorityEntity> = HashSet(),
)
