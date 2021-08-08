package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.common.Constants
import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Schema(description = "Registration")
data class RegistrationDto (

    @Schema(example = "ttesterson", required = true)
    var login: @NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String,

    @Schema(example = "test@example.com", required = true)
    var email: @NotNull @Email @Size(min = 5, max = 254) String,

    @Schema(example = "p@ssword", required = true)
    var password: @NotNull @Size(min = 60, max = 60) String,

    @Schema(example = "Test", required = true)
    var firstName: @NotNull @Size(max = 50) String,

    @Schema(example = "Testerson", required = true)
    var lastName: @Size(max = 50) String?,

    @Schema(example = "en_US", required = true)
    var langKey: @Size(min = 2, max = 10) String = Constants.DEFAULT_LANGUAGE,

    @Schema(example = "AuthoritiesConstants.USER", required = true)
    var authorities: Set<String> = setOf(AuthoritiesConstants.USER),
)