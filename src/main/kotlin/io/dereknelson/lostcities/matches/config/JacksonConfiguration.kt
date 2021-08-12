package io.dereknelson.lostcities.matches.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.problem.ProblemModule
import org.zalando.problem.validation.ConstraintViolationProblemModule

@Configuration
class JacksonConfiguration {

    @Autowired
    fun updateMapper(objectMapper: ObjectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    fun javaTimeModule(): JavaTimeModule {
        return JavaTimeModule()
    }

    @Bean
    fun jdk8TimeModule(): Jdk8Module {
        return Jdk8Module()
    }

    @Bean
    fun hibernate5Module(): Hibernate5Module {
        return Hibernate5Module()
    }

    @Bean
    fun problemModule(): ProblemModule {
        return ProblemModule().withStackTraces()
    }

    @Bean
    fun constraintViolationProblemModule(): ConstraintViolationProblemModule {
        return ConstraintViolationProblemModule()
    }
}