package io.dereknelson.lostcities.matches.api

import io.dereknelson.lostcities.common.model.match.UserPair
import io.swagger.v3.oas.annotations.media.Schema

class MatchDto(
    val id: Long? = null,
    val players: UserPair,
    val currentPlayer: String? = null,
    @Schema(example = "false", required = true)
    val isReady: Boolean = false,

    @Schema(example = "false", required = true)
    val isStarted: Boolean = false,

    @Schema(example = "false", required = true)
    val isCompleted: Boolean = false,
)
