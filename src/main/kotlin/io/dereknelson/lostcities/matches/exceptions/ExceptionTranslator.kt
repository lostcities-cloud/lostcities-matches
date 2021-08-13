package io.dereknelson.lostcities.matches.exceptions

import io.dereknelson.lostcities.common.SpringProfileConstants
import org.apache.commons.lang3.StringUtils
import org.springframework.core.env.Environment
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataAccessException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.NativeWebRequest
import org.zalando.problem.*
import org.zalando.problem.spring.web.advice.ProblemHandling
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem
import java.net.URI
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@ControllerAdvice
class ExceptionTranslator(private val env: Environment) : ProblemHandling, SecurityAdviceTrait {
    private val applicationName = "lost-cities"

    /**
     * Post-process the Problem payload to add the message key for the front-end if needed.
     */
    override fun process(entity: ResponseEntity<Problem>, request: NativeWebRequest): ResponseEntity<Problem>? {
        val problem: Problem = entity.body!!
        if (!(problem is ConstraintViolationProblem || problem is DefaultProblem)) {
            return entity
        }
        val nativeRequest: HttpServletRequest? = request.getNativeRequest(
            HttpServletRequest::class.java
        )
        val requestUri = if (nativeRequest != null) nativeRequest.requestURI else StringUtils.EMPTY
        val builder: ProblemBuilder = Problem
            .builder()
            .withType(if (Problem.DEFAULT_TYPE == problem.type) ErrorConstants.DEFAULT_TYPE else problem.type)
            .withStatus(problem.status)
            .withTitle(problem.title)
            .with(PATH_KEY, requestUri)
        if (problem is ConstraintViolationProblem) {
            builder
                .with(VIOLATIONS_KEY, (problem).violations)
                .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION)
        } else {
            builder.withCause((problem as DefaultProblem).cause).withDetail(problem.detail)
                .withInstance(problem.instance)
            problem.parameters.forEach { (key: String?, value: Any?) -> builder.with(key, value) }
            if (!problem.parameters.containsKey(MESSAGE_KEY) && problem.status != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.status!!.statusCode)
            }
        }
        return ResponseEntity<Problem>(builder.build(), entity.getHeaders(), entity.getStatusCode())
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> {
        val result: BindingResult = ex.bindingResult
        val fieldErrors: List<FieldError> = result
            .fieldErrors
            .stream()
            .map { f: FieldError ->
                    val code : String = if (StringUtils.isNotBlank(f.defaultMessage))
                        f.defaultMessage!!
                    else
                        f.code!!

                    FieldError(
                        f.objectName.replaceFirst("DTO$".toRegex(), ""),
                        f.field,
                        code
                    )
                }
                .collect(Collectors.toList())
        val problem: Problem = Problem
            .builder()
            .withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
            .withTitle("Method argument not valid")
            .withStatus(defaultConstraintViolationStatus())
            .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION)
            .with(FIELD_ERRORS_KEY, fieldErrors)
            .build()
        return create(ex, problem, request)
    }

    @ExceptionHandler
    fun handleConcurrencyFailure(
        ex: ConcurrencyFailureException?,
        request: NativeWebRequest?
    ): ResponseEntity<Problem> {
        val problem: Problem =
            Problem.builder().withStatus(Status.CONFLICT).with(MESSAGE_KEY, ErrorConstants.ERR_CONCURRENCY_FAILURE)
                .build()
        return create(ex!!, problem, request!!)
    }

    fun prepare(throwable: Throwable, status: StatusType, type: URI?): ProblemBuilder {
        val activeProfiles: Collection<String> = listOf(*env.activeProfiles)
        if (activeProfiles.contains(SpringProfileConstants.SPRING_PROFILE_PRODUCTION)) {
            if (throwable is HttpMessageConversionException) {
                return Problem
                    .builder()
                    .withType(type)
                    .withTitle(status.reasonPhrase)
                    .withStatus(status)
                    .withDetail("Unable to convert http message")
                    .withCause(
                        Optional.ofNullable(throwable.cause)
                            .filter { isCausalChainsEnabled }
                            .map { this.toProblem(it) }
                            .orElse(null)
                    )
            }
            if (throwable is DataAccessException) {
                return Problem
                    .builder()
                    .withType(type)
                    .withTitle(status.reasonPhrase)
                    .withStatus(status)
                    .withDetail("Failure during data access")
                    .withCause(
                        Optional.ofNullable(throwable.cause)
                            .filter{ isCausalChainsEnabled }
                            .map{ this.toProblem(it) }
                            .orElse(null)
                    )
            }
            if (containsPackageName(throwable.message)) {
                return Problem
                    .builder()
                    .withType(type)
                    .withTitle(status.getReasonPhrase())
                    .withStatus(status)
                    .withDetail("Unexpected runtime exception")
                    .withCause(
                        Optional.ofNullable(throwable.cause)
                            .filter { isCausalChainsEnabled }
                            .map { this.toProblem(it) }
                            .orElse(null)
                    )
            }
        }
        return Problem
            .builder()
            .withType(type)
            .withTitle(status.reasonPhrase)
            .withStatus(status)
            .withDetail(throwable.message)
            .withCause(
                Optional.ofNullable(throwable.cause)
                    .filter { isCausalChainsEnabled }
                    .map { this.toProblem(it) }
                    .orElse(null)
            )
    }

    private fun containsPackageName(message: String?): Boolean {
        // This list is for sure not complete
        return StringUtils.containsAny(
            message,
            "org.",
            "java.",
            "net.",
            "javax.",
            "com.",
            "io.",
            "de.",
            "io.dereknelson."
        )
    }

    companion object {
        private const val FIELD_ERRORS_KEY = "fieldErrors"
        private const val MESSAGE_KEY = "message"
        private const val PATH_KEY = "path"
        private const val VIOLATIONS_KEY = "violations"
    }
}