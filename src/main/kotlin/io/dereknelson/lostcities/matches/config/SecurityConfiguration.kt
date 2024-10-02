package io.dereknelson.lostcities.matches.config

import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.dereknelson.lostcities.common.auth.JwtFilter
import io.dereknelson.lostcities.common.auth.TokenProvider
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.web.filter.ForwardedHeaderFilter


@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SecurityScheme(
    name = "jwt_auth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer"
)
class SecurityConfiguration(
    private val tokenProvider: TokenProvider,
) {
    @Bean
    fun forwardedHeaderFilter(): ForwardedHeaderFilter? {
        return ForwardedHeaderFilter()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer {
            it
                .ignoring()
                .requestMatchers(AntPathRequestMatcher("/management/health"))
                .requestMatchers(antMatcher(HttpMethod.OPTIONS,"/**"))
                .requestMatchers(antMatcher("/app/**/*.{js,html}"))
                .requestMatchers(antMatcher("/i18n/**"))
                .requestMatchers(antMatcher("/content/**"))
                .requestMatchers(antMatcher("/swagger-ui/**"))
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain {
        /* ktlint-disable max_line_length */
        // @formatter:off

        http.csrf { it.init(http) }
            .cors { it.configure(http) }
            .addFilterBefore(JwtFilter(tokenProvider), AnonymousAuthenticationFilter::class.java)
            .exceptionHandling {}
            .headers { headersConfigurer ->
                headersConfigurer.contentSecurityPolicy {
                    it.policyDirectives("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                }.referrerPolicy {
                    it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                }.cacheControl {  }

            }

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(AntPathRequestMatcher("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(AntPathRequestMatcher("/api/**")).hasAuthority(AuthoritiesConstants.USER)
                    .requestMatchers(AntPathRequestMatcher("/actuator/swagger-ui/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/actuator/openapi/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/actuator/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/actuator/health")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/actuator/health/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/actuator/info")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/actuator/prometheus")).permitAll()
                    //.requestMatchers(AntPathRequestMatcher("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            }
        // @formatter:on
        /* ktlint-enable max_line_length */
        return http.build()
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
