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
	"io.dereknelson.lostcities.domains",
	"io.dereknelson.lostcities.api",
)
@EnableConfigurationProperties(KafkaConfiguration::class)
@SpringBootApplication(exclude=[ErrorMvcAutoConfiguration::class])
class LostcitiesApplication

fun main(args: Array<String>) {
	runApplication<LostcitiesApplication>(*args)
}
