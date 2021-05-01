package io.dereknelson.lostcities.api.matches

import io.dereknelson.lostcities.concerns.matches.UserPair

class MatchDto(
    val players: UserPair,
    val isReady: Boolean = false,
    val isStarted: Boolean = false,
    val isCompleted: Boolean = false
)