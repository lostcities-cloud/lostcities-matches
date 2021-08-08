package io.dereknelson.lostcities.api.matches

import io.dereknelson.lostcities.domains.matches.UserPair
import io.swagger.v3.oas.annotations.media.Schema

class MatchDto(
    val players: UserPair,

    @Schema(example = "false", required = true)
    val isReady: Boolean = false,

    @Schema(example = "false", required = true)
    val isStarted: Boolean = false,

    @Schema(example = "false", required = true)
    val isCompleted: Boolean = false
)