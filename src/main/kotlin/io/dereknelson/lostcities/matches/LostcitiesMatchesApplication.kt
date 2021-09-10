package io.dereknelson.lostcities.matches

import io.dereknelson.lostcities.matches.events.EventProperties
import io.dereknelson.lostcities.matches.events.KafkaConfiguration
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableJpaRepositories
@ComponentScan(
	"io.dereknelson.lostcities.matches.library",
	"io.dereknelson.lostcities.matches.config",
	"io.dereknelson.lostcities.matches.persistence",
	"io.dereknelson.lostcities.matches.api",
	"io.dereknelson.lostcities.matches.events",
	"io.dereknelson.lostcities.matches.service",
	"io.dereknelson.lostcities.common.library"
)
@EnableConfigurationProperties(EventProperties::class)
@SpringBootApplication(exclude=[ErrorMvcAutoConfiguration::class])
@OpenAPIDefinition(servers = [Server(url="lostcities.com")])
class LostcitiesMatchesApplication

fun main(args: Array<String>) {
	runApplication<LostcitiesMatchesApplication>(*args)
}
