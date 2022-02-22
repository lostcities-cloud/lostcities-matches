package io.dereknelson.lostcities.matches.config

import org.springframework.context.annotation.Lazy
import io.dereknelson.lostcities.matches.service.MatchService.Companion.CREATE_GAME_QUEUE
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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


            val queue = Queue(CREATE_GAME_QUEUE, true, false, false)
            val binding = Binding(CREATE_GAME_QUEUE, Binding.DestinationType.QUEUE, exchange, "*", null);
            admin.declareQueue(queue);
            admin.declareBinding(binding);
        } catch (e: Exception) {
            println("Alright.")
        }
    }



}