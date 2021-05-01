package io.dereknelson.lostcities

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories
@ComponentScan(
	"io.dereknelson.lostcities.library.security",
	"io.dereknelson.lostcities.api",
	"io.dereknelson.lostcities.concerns"
)
@SpringBootApplication
class LostcitiesApplication

fun main(args: Array<String>) {
	runApplication<LostcitiesApplication>(*args)
}
