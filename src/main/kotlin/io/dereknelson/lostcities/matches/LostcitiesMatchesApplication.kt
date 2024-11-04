package io.dereknelson.lostcities.matches

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.dereknelson.lostcities.common.WebConfigProperties
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
@OpenAPIDefinition(
    servers = [
        Server(url = "lostcities.com"),
        Server(url = "matches.lostcities.com"),
    ],
)
@EnableConfigurationProperties(WebConfigProperties::class)
@ComponentScan(
    "io.dereknelson.lostcities.matches.rank",
    "io.dereknelson.lostcities.matches",
    "io.dereknelson.lostcities.common",
)
@EnableJpaRepositories
@EnableScheduling
@EntityScan("io.dereknelson.lostcities.matches")
class LostcitiesMatchesApplication

@Bean
fun mapper() = jacksonObjectMapper().registerKotlinModule()

fun main(args: Array<String>) {
    runApplication<LostcitiesMatchesApplication>(*args)
}
