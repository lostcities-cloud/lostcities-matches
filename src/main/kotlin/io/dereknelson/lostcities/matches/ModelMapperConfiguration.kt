package io.dereknelson.lostcities.matches

import io.dereknelson.lostcities.common.model.match.Match
import io.dereknelson.lostcities.matches.persistence.MatchEntity
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.common.model.User
import io.dereknelson.lostcities.common.model.UserRef

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

@Configuration
class ModelMapperConfiguration {

    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()

        modelMapper.addConverter({ context ->
            if(context.source == null) {
                null
            } else {
                val src: MatchEntity = context.source as MatchEntity

                val match = Match(
                    id = src.id!!,
                    seed = src.seed!!,
                    players = UserPair(
                        user1 = null,
                        user2 = null,
                        score1 = src.score1,
                        score2 = src.score2
                    ),
                    concededBy = modelMapper.map(src.concededBy, User::class.java),
                    isReady = src.isReady!!,
                    isStarted = src.isStarted!!,
                    isCompleted = src.isCompleted!!,
                    createdDate = LocalDateTime.ofInstant(src.createdDate, ZoneOffset.UTC),
                    lastModifiedDate = LocalDateTime.ofInstant(src.lastModifiedDate, ZoneOffset.UTC),
                    createdBy = src.createdBy!!,
                )

                if (src.player1 == null) {
                    match.players.user1 = User(src.player1?.id, src.player1?.login!!, src.player1?.email!!)
                }

                if (src.player1 == null) {
                    match.players.user2 = User(src.player1?.id, src.player1?.login!!, src.player1?.email!!)
                }

                match
            }
        }, MatchEntity::class.java, Match::class.java)

        modelMapper.addConverter({ context ->
            if(context.source == null) {
                null
            } else {
                val src: Match = context.source as Match

                val match = MatchEntity(
                    id= src.id,
                    player1 = null,
                    player2 = null,
                    score1 = src.players.score1,
                    score2 = src.players.score2,
                    isReady = src.isReady,
                    isStarted = src.isStarted,
                    isCompleted = src.isCompleted,
                )

                if (src.players.user1 == null) {
                    match.player1 =
                        UserRef(src.players.user1?.id, src.players.user1?.login!!, src.players.user1?.email!!)
                }

                if (src.players.user2 == null) {
                    match.player2 =
                        UserRef(src.players.user2?.id, src.players.user2?.login!!, src.players.user2?.email!!)
                }

                if(src.concededBy != null) {
                    match.concededBy = UserRef(src.concededBy!!.id, src.concededBy!!.login, src.concededBy!!.email)
                }

                match
            }
        }, Match::class.java, MatchEntity::class.java)

        modelMapper.addConverter({ context ->
            if(context.source == null) {
                null
            } else {
                val src: Timestamp = context.source as Timestamp

                val dest = src.toLocalDateTime()
                dest.atOffset(ZoneOffset.UTC)
                dest
            }
        }, Timestamp::class.java, LocalDateTime::class.java)

        return modelMapper
    }
}