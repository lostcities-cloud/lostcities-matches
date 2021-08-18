package io.dereknelson.lostcities.matches.events

import io.dereknelson.lostcities.matches.api.MatchDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service


@Service
class CreateGameEventService(
    private var eventProperties: EventProperties,
    private var kafkaTemplate: KafkaTemplate<String, MatchDto>
) {

    fun createGame(matchDto: MatchDto) {
        kafkaTemplate.send(eventProperties.createGameTopic, matchDto)
    }

}