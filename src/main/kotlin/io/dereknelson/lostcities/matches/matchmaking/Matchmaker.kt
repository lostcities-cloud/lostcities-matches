package io.dereknelson.lostcities.matches.matchmaking

import io.dereknelson.lostcities.matches.MatchService
import io.dereknelson.lostcities.matches.match.MatchEntity
import io.dereknelson.lostcities.matches.match.MatchRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.concurrent.TimeUnit

@Component
class Matchmaker(
    val matchService: MatchService,
    val matchRepository: MatchRepository,
) {
    @Scheduled(fixedRate = 6, timeUnit = TimeUnit.HOURS)
    fun retryMaxAttemptMatches() {
        var pageable: Pageable = PageRequest.of(
            0, 100, Sort.by(Sort.Direction.ASC, "createdDate")
        )

        var page: Page<MatchEntity>? = null
        while (page == null || page.hasNext()) {
            page = matchService.findMaxAttemptMatches(pageable)
            retryMatchPairs(page)
            pageable = page.nextPageable()
        }
    }

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

    fun retryMatchPairs(page: Page<MatchEntity>) {
        val toRetry = page.content.map { match ->
            match.matchMakingCount = 0
            match
        }

        matchRepository.saveAllAndFlush(toRetry)
    }

    fun findMatchPair(match: MatchEntity): Optional<MatchEntity> {
        if (match.matchMakingCount > 1000) {
            return matchService.findUnrankedMatch(match)
        }

        return matchService.findMatchMakingMatchInRange(match, 100)
    }
}
