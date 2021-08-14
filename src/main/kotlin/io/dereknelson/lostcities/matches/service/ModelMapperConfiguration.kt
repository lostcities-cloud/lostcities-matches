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
                val src: Timestamp = context.source as Timestamp

                val dest = src.toLocalDateTime()
                dest.atOffset(ZoneOffset.UTC)
                dest
            }
        }, Timestamp::class.java, LocalDateTime::class.java)

        return modelMapper
    }
}