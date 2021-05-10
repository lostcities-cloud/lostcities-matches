package io.dereknelson.lostcities.api.matches

import io.dereknelson.lostcities.concerns.matches.UserPair
import io.swagger.annotations.ApiModelProperty

class MatchDto(


    val players: UserPair,

    @ApiModelProperty(example = "false", required = true, position = 1)
    val isReady: Boolean = false,

    @ApiModelProperty(example = "false", required = true, position = 2)
    val isStarted: Boolean = false,

    @ApiModelProperty(example = "false", required = true, position = 3)
    val isCompleted: Boolean = false
)