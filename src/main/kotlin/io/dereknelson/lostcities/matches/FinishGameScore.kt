package io.dereknelson.lostcities.matches

import org.springframework.context.ApplicationEvent

class FinishGameScore(
    var player1Name: String,
    var player1Score: Int,
    var player2Name: String,
    var player2Score: Int,
) {
    fun asEvent(): FinishGameEvent {
        return FinishGameEvent(this)
    }
}

class FinishGameEvent(finishGameScore: FinishGameScore) : ApplicationEvent(finishGameScore)
