package io.dereknelson.lostcities.domains.user

import io.dereknelson.lostcities.domains.user.entity.AuthorityEntity
import io.dereknelson.lostcities.common.Constants
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class Registration (
    val login: String,
    val email: String,
    var password: @NotNull @Size(min = 60, max = 60) String,
    val firstName: @NotNull @Size(max = 50) String,
    val lastName: @Size(max = 50) String?,
    val langKey: @NotNull @Size(min = 2, max = 10) String = Constants.DEFAULT_LANGUAGE,
    var authorities: @Size(min = 1) Set<AuthorityEntity> = HashSet(),
)
