package io.dereknelson.lostcities.api.users

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "User")
data class UserDto(
    @ApiModelProperty(example = "1", required = true, position = 0)
    var id: Long?=null,

    @ApiModelProperty(example = "ttesterson", required = true, position = 1)
    val login: String?=null,

    @ApiModelProperty(example = "test@example.com", required = true, position = 2)
    val email: String?=null,

    @ApiModelProperty(example = "en_US", required = true, position = 3)
    val langKey: String="en_US"
)