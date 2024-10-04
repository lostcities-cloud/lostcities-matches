package io.dereknelson.lostcities.matches

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.dereknelson.lostcities.common.WebConfigProperties
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.util.pattern.PathPatternParser

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])

@OpenAPIDefinition(
    servers = [
        Server(url = "lostcities.com"),
        Server(url = "matches.lostcities.com")
    ]
)
@EnableConfigurationProperties(WebConfigProperties::class)
@ComponentScan(

    "io.dereknelson.lostcities.matches",
    "io.dereknelson.lostcities.matches.library",
    "io.dereknelson.lostcities.matches.config",
    "io.dereknelson.lostcities.matches.persistence",
    "io.dereknelson.lostcities.matches.api",
    "io.dereknelson.lostcities.common",
)

@EnableJpaRepositories
@EnableRabbit
class LostcitiesMatchesApplication

@Bean
fun mapper() = jacksonObjectMapper().registerKotlinModule()

fun main(args: Array<String>) {
    runApplication<LostcitiesMatchesApplication>(*args)
}


