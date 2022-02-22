package io.dereknelson.lostcities.matches

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])

@OpenAPIDefinition(
    servers = [
        Server(url = "lostcities.com"),
        Server(url = "matches.lostcities.com")
    ]
)

@ComponentScan(
    "io.dereknelson.lostcities.matches",
    "io.dereknelson.lostcities.matches.library",
    "io.dereknelson.lostcities.matches.config",
    "io.dereknelson.lostcities.matches.persistence",
    "io.dereknelson.lostcities.matches.api",
    "io.dereknelson.lostcities.matches.service",
    "io.dereknelson.lostcities.common.auth",
    "io.dereknelson.lostcities.common.library"
)

@EnableJpaRepositories
@EnableRabbit
class LostcitiesMatchesApplication

fun main(args: Array<String>) {
    runApplication<LostcitiesMatchesApplication>(*args)
}
