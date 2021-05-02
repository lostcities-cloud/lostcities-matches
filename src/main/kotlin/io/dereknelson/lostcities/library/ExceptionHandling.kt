package io.dereknelson.lostcities.library

import org.springframework.web.bind.annotation.ControllerAdvice
import org.zalando.problem.spring.web.advice.ProblemHandling

@ControllerAdvice
internal class ExceptionHandling : ProblemHandling {
    override fun isCausalChainsEnabled(): Boolean {
        return true
    }
}