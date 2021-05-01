package io.dereknelson.lostcities.concerns.matches

import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class MatchService {
    @Autowired
    private lateinit var modelMapper: ModelMapper
    @Autowired
    private lateinit var matchRepository : MatchRepository

    private val random : Random = Random()

    fun markAsStarted(match: Match ): Boolean {
        return matchRepository.findById(match.id)
            .filter { it.isReady!! }
            .map { matchEntity: MatchEntity ->
                matchEntity.isStarted = true
                true
            }
            .orElse(false)
    }

    fun markAsCompleted(match: Match): Boolean {
        return matchRepository.findById(match.id)
            .filter { it.isReady!! && it.isStarted!! }
            .map { matchEntity: MatchEntity ->
                matchEntity.isCompleted = true
                true
            }
            .orElse(false)
    }

    fun findById(id: Long): Optional<Match> {
        return matchRepository.findById(id).map { modelMapper.map(it, Match::class.java) }
    }

    //fun findAvailableForUser(userDetails: UserDetails): Optional<Match> {
        //return matchRepository.getAvailableGamesForPlayer(userDetails).map { modelMapper.map(it, Match::class.java) }
    //}

    fun create(match: Match): Match {
        var matchEntity: MatchEntity = modelMapper.map(match, MatchEntity::class.java)
        matchEntity.seed = random.nextLong()

        return modelMapper.map(matchRepository.save(matchEntity), Match::class.java)
    }

    fun delete(match: Match) {
        val matchEntity : MatchEntity = modelMapper.map(match, MatchEntity::class.java)
        matchRepository.delete(matchEntity)
    }
}