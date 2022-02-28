package io.dereknelson.lostcities.matches.config

import io.dereknelson.lostcities.matches.service.MatchEventService.Companion.END_GAME_EVENT
import io.dereknelson.lostcities.matches.service.MatchEventService.Companion.TURN_CHANGE_EVENT
import io.dereknelson.lostcities.matches.service.MatchService.Companion.CREATE_GAME_QUEUE
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct

@Lazy(false)
@Configuration
class RabbitConfiguration(
    template: RabbitTemplate,
) {
    val exchange: String = ""

    @get:Bean
    val admin: RabbitAdmin = RabbitAdmin(template)

    @PostConstruct
    fun initialize() {
        try {
            declare(
                Queue(CREATE_GAME_QUEUE, true, false, false),
                Binding(CREATE_GAME_QUEUE, Binding.DestinationType.QUEUE, exchange, "*", null)
            )

            declare(
                Queue(TURN_CHANGE_EVENT, true, false, false),
                Binding(TURN_CHANGE_EVENT, Binding.DestinationType.QUEUE, exchange, "*", null)
            )

            declare(
                Queue(END_GAME_EVENT, true, false, false),
                Binding(END_GAME_EVENT, Binding.DestinationType.QUEUE, exchange, "*", null)
            )
        } catch (e: Exception) {
            println("Alright.")
        }
    }

    private fun declare(queue: Queue, binding: Binding) {
        admin.declareQueue(queue)
        admin.declareBinding(binding)
    }
}
