package io.dereknelson.lostcities.matches.service

import io.dereknelson.lostcities.common.auth.entity.UserRef
import io.dereknelson.lostcities.common.model.match.Match
import io.dereknelson.lostcities.matches.persistence.MatchEntity
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.common.model.User


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
                        user1 = src.player1,
                        user2 = src.player2,
                        score1 = src.score1,
                        score2 = src.score2
                    ),
                    concededBy = src.concededBy,
                    isReady = src.isReady!!,
                    isStarted = src.isStarted!!,
                    isCompleted = src.isCompleted!!,
                    createdDate = LocalDateTime.ofInstant(src.createdDate, ZoneOffset.UTC),
                    lastModifiedDate = LocalDateTime.ofInstant(src.lastModifiedDate, ZoneOffset.UTC),
                    createdBy = src.createdBy!!,
                )

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

                match.player1 = src.players.user1
                match.player2 = src.players.user2
                match.concededBy = src.concededBy


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