package io.dereknelson.lostcities.matches.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.problem.ProblemModule
import org.zalando.problem.validation.ConstraintViolationProblemModule

@Configuration
class JacksonConfiguration {

    @Bean
    fun mapper() =
        jacksonObjectMapper()
            .registerKotlinModule()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)!!

    @Bean
    fun javaTimeModule() = JavaTimeModule()


    @Bean
    fun jdk8TimeModule() = Jdk8Module()


    @Bean
    fun hibernate5Module() = Hibernate5Module()


    @Bean
    fun problemModule() = ProblemModule().withStackTraces()!!


    @Bean
    fun constraintViolationProblemModule() = ConstraintViolationProblemModule()

}
