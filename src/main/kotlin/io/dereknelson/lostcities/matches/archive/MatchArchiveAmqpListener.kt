package io.dereknelson.lostcities.matches.archive

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class MatchArchiveAmqpListener {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    companion object {
        const val ARCHIVE_MATCH_EVENT = "archive-match"
        const val ARCHIVE_MATCH_EVENT_DLQ = "archive-match-dlq"
        const val REQUEST_ARCHIVED_MATCH_EVENT = "request-archived-match"
        const val REQUEST_ARCHIVED_MATCH_EVENT_DLQ = "request-archived-match-dlq"
    }

    @Bean
    @Qualifier(ARCHIVE_MATCH_EVENT)
    fun archiveMatch() = QueueBuilder
        .durable(ARCHIVE_MATCH_EVENT)
        .quorum()
        .ttl(5000)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", ARCHIVE_MATCH_EVENT_DLQ)
        .build()!!

    @Bean
    @Qualifier(ARCHIVE_MATCH_EVENT_DLQ)
    fun archiveMatchDlq() = QueueBuilder
        .durable(ARCHIVE_MATCH_EVENT_DLQ)
        .quorum()
        .build()!!

    @Bean
    @Qualifier(REQUEST_ARCHIVED_MATCH_EVENT)
    fun requestArchived() = QueueBuilder
        .durable(REQUEST_ARCHIVED_MATCH_EVENT)
        .quorum()
        .ttl(5000)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", REQUEST_ARCHIVED_MATCH_EVENT_DLQ)
        .build()!!

    @Bean
    @Qualifier(REQUEST_ARCHIVED_MATCH_EVENT_DLQ)
    fun turnChangeEventDLQueue() = QueueBuilder
        .durable(REQUEST_ARCHIVED_MATCH_EVENT_DLQ)
        .quorum()
        .build()!!

    @RabbitListener(queues = [ARCHIVE_MATCH_EVENT], exclusive = true)
    fun gameEvent(gameMessage: Message) {
    }
}
