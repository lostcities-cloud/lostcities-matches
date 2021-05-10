package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.library.Constants
import io.dereknelson.lostcities.library.security.AuthoritiesConstants
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@ApiModel(description = "Registration")
data class RegistrationDto (

    @ApiModelProperty(example = "ttesterson", required = true, position = 0)
    var login: @NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String,

    @ApiModelProperty(example = "test@example.com", required = true, position = 1)
    var email: @NotNull @Email @Size(min = 5, max = 254) String,

    @ApiModelProperty(example = "p@ssword", required = true, position = 2)
    var password: @NotNull @Size(min = 60, max = 60) String,

    @ApiModelProperty(example = "Test", required = true, position = 3)
    var firstName: @NotNull @Size(max = 50) String,

    @ApiModelProperty(example = "Testerson", required = true, position = 4)
    var lastName: @Size(max = 50) String?,

    @ApiModelProperty(example = "en_US", required = true, position = 5)
    var langKey: @Size(min = 2, max = 10) String = "en_US",

    @ApiModelProperty(example = "[${AuthoritiesConstants.USER}]", required = true, position = 6)
    var authorities: Set<String> = setOf(AuthoritiesConstants.USER),
)