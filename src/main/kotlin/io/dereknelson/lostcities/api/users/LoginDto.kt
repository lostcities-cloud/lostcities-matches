package io.dereknelson.lostcities.api.users

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Schema(description = "Login")
class LoginDto(
    @Schema(example = "ttesterson", required = true)
    val username: @NotNull @Size(min = 1, max = 50) String? = null,

    @Schema(example = "test@example.com", required = true)
    val password: @NotNull @Size(min = 4, max = 100) String? = null,

    @Schema(example = "test@example.com", required = false, defaultValue = "false")
    val rememberMe: Boolean = false
)