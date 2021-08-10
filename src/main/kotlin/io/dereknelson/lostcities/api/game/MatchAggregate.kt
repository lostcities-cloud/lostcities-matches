package io.dereknelson.lostcities.api.game

import io.dereknelson.lostcities.common.model.game.Command
import io.dereknelson.lostcities.common.model.match.Match

class MatchAggregate(
    val matchId: Long,
    val match: Match,
    val command: Command
)

