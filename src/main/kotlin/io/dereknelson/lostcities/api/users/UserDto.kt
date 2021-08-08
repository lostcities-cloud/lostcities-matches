package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.common.Constants
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "User")
data class UserDto(
    @Schema(example = "1", required = true)
    var id: Long?=null,

    @Schema(example = "ttesterson", required = true)
    val login: String?=null,

    @Schema(example = "test@example.com", required = true)
    val email: String?=null,

    @Schema(example = "en_US", required = true)
    val langKey: String= Constants.DEFAULT_LANGUAGE
)