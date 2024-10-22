package io.dereknelson.lostcities.matches.config

import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.dereknelson.lostcities.common.WebConfigProperties
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
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SecurityScheme(
    name = "jwt_auth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
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
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .requestMatchers(antMatcher(HttpMethod.GET, "/actuator/**"))
                .requestMatchers(
                    "/health",
                    "/i18n/**",
                    "/content/**",
                    "/swagger-ui/**",
                )
        }
    }

    @Bean
    fun corsMappingConfigurer(webConfigProperties: WebConfigProperties): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                val cors: WebConfigProperties.Cors = webConfigProperties.cors
                registry.addMapping("/**")
                    .allowedOrigins(*cors.allowedOrigins)
                    .allowedMethods(*cors.allowedMethods)
                    .maxAge(cors.maxAge)
                    .allowedHeaders(*cors.allowedHeaders)
                    .exposedHeaders(*cors.exposedHeaders)
            }
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain {
        // @formatter:off

        http.csrf { it.disable() }
            .cors { it.configure(http) }
            .addFilterBefore(JwtFilter(tokenProvider), AnonymousAuthenticationFilter::class.java)
            .exceptionHandling {}
            .headers { headersConfigurer ->
                headersConfigurer.contentSecurityPolicy {
                    it.policyDirectives("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                }.referrerPolicy {
                    it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                }.cacheControl { }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(AntPathRequestMatcher("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(AntPathRequestMatcher("/matches")).hasAuthority(AuthoritiesConstants.USER)
                    .requestMatchers(AntPathRequestMatcher("/management/matches")).permitAll()
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/openapi/**",
                        "/actuator/**",

                    ).permitAll()
                    .anyRequest().authenticated()
            }

        return http.build()!!
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
