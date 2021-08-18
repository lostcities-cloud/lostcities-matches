package io.dereknelson.lostcities.matches.events

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(value="kafka")
class EventProperties {
    lateinit var server: String
    lateinit var createGameTopic: String
}