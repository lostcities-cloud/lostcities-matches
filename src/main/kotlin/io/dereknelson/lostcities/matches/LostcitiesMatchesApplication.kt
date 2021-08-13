package io.dereknelson.lostcities.matches

import io.dereknelson.lostcities.matches.config.KafkaConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableJpaRepositories
@ComponentScan(
	"io.dereknelson.lostcities.matches.library.security",
	"io.dereknelson.lostcities.matches.config",
	"io.dereknelson.lostcities.matches.persistence",
	"io.dereknelson.lostcities.matches.api",
	"io.dereknelson.lostcities.matches.service",
	"io.dereknelson.lostcities.common.auth"
)
//@EnableConfigurationProperties(KafkaConfiguration::class)
@SpringBootApplication(exclude=[ErrorMvcAutoConfiguration::class])
class LostcitiesMatchesApplication

fun main(args: Array<String>) {
	runApplication<LostcitiesMatchesApplication>(*args)
}
