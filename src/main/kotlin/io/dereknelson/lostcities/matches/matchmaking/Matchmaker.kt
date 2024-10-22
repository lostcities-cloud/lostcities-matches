package io.dereknelson.lostcities.matches.matchmaking

import io.dereknelson.lostcities.matches.MatchService
import io.dereknelson.lostcities.matches.match.MatchEntity
import io.dereknelson.lostcities.matches.match.MatchRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class Matchmaker(
    val matchService: MatchService,
    val matchRepository: MatchRepository,
) {

    @Scheduled(fixedDelay = 2000)
    @Transactional
    fun matchMake() {
        val toMatch = matchService.findUnrankedMatch()
        if (toMatch.isEmpty) {
            println("No matches available")
            return
        }

        val match1 = toMatch.get()
        val matching = findMatchPair(match1)

        if (matching.isEmpty) {
            println("Unable to find match for ${match1.id}")
            matchService.increment(match1)
            return
        }

        val match2 = matching.get()
        matchService.joinMatch(match1, match2.player1)

        matchRepository.delete(match2)
    }

    fun findMatchPair(match: MatchEntity): Optional<MatchEntity> {
        if (match.matchMakingCount > 1000) {
            return matchService.findUnrankedMatch(match)
        }

        return matchService.findMatchMakingMatchInRange(match, 100)
    }
}
