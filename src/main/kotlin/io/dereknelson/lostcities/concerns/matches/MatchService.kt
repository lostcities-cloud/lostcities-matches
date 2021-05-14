package io.dereknelson.lostcities.concerns.matches

import io.dereknelson.lostcities.common.User
import io.dereknelson.lostcities.common.UserRef
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*

@Service
class MatchService {
    @Autowired
    private lateinit var modelMapper: ModelMapper
    @Autowired
    private lateinit var matchRepository : MatchRepository

    private val random : Random = Random()

    fun markStarted(match: Match): Match {
        var matchEntity = modelMapper.map(match, MatchEntity::class.java)

        if (matchEntity.isReady!!) {
            throw RuntimeException("Unable to start match [${match.id}]")
        } else {
            matchEntity.isStarted = true
            matchEntity = matchRepository.save(matchEntity)

            return modelMapper.map(matchEntity, Match::class.java)
        }
    }

    fun markAsCompleted(match: Match): Match {
        var matchEntity = modelMapper.map(match, MatchEntity::class.java)

        if (matchEntity.isCompleted!!) {
            throw RuntimeException("Unable to complete match [${match.id}]")
        } else {
            matchEntity.isCompleted = true
            matchEntity = matchRepository.save(matchEntity)

            return modelMapper.map(matchEntity, Match::class.java)
        }
    }

    fun concede(match: Match, user: User): Match {
        var matchEntity = modelMapper.map(match, MatchEntity::class.java)

        if (matchEntity.concededBy != null) {
            throw RuntimeException("Unable to complete match [${match.id}]")
        } else {
            matchEntity.isCompleted = true
            matchEntity.concededBy = UserRef(user.id, user.login, user.email)
            matchEntity = matchRepository.save(matchEntity)

            return modelMapper.map(matchEntity, Match::class.java)
        }
    }

    fun findById(id: Long): Optional<Match> {
        return matchRepository.findById(id)
            .map { modelMapper.map(it, Match::class.java) }
    }

    fun create(match: Match): Match {
        val matchEntity: MatchEntity = modelMapper.map(match, MatchEntity::class.java)
        matchEntity.seed = random.nextLong()

        return modelMapper.map(matchRepository.save(matchEntity), Match::class.java)
    }

    private fun delete(match: Match) {
        val matchEntity : MatchEntity = modelMapper.map(match, MatchEntity::class.java)
        matchRepository.delete(matchEntity)
    }

    fun joinMatch(match: Match, user: User): Match {
        if(match.players.contains(user) || match.players.isPopulated) {
            throw UnableToJoinMatchException()
        } else {
            match.players.user2 = user
        }

        val matchEntity : MatchEntity = modelMapper.map(match, MatchEntity::class.java)
        return modelMapper.map(matchRepository.save(matchEntity), Match::class.java)
    }
}