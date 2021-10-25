package io.dereknelson.lostcities.matches.config

import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.dereknelson.lostcities.common.auth.JwtFilter
import io.dereknelson.lostcities.common.auth.TokenProvider

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.context.SecurityContextPersistenceFilter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.web.filter.CorsFilter
import org.springframework.web.filter.ForwardedHeaderFilter
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport
import javax.servlet.Filter


@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport::class)
@SecurityScheme(name = "jwt_auth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
class SecurityConfiguration(
    private val tokenProvider: TokenProvider,
    private val corsFilter: CorsFilter,
    private val problemSupport: SecurityProblemSupport,
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun forwardedHeaderFilter(): ForwardedHeaderFilter? {
        return ForwardedHeaderFilter()
    }

    override fun configure(web: WebSecurity) {

        web
            .ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/**")
            .antMatchers("/test/**")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // @formatter:off
        http

            .cors()
            .and()
            .csrf()
            .disable()

            .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
            .and()
                .headers()
                .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
            .and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
                .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
            .and()
                .frameOptions()
                .deny()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()

                .antMatchers("/api/account/reset-password/init").permitAll()
                .antMatchers("/api/account/reset-password/finish").permitAll()
                .antMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api/**").permitAll()
                .antMatchers("/management/health").permitAll()
                .antMatchers("/management/health/**").permitAll()
                .antMatchers("/management/info").permitAll()
                .antMatchers("/management/prometheus").permitAll()
                .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .and().addFilterAfter(JwtFilter(tokenProvider), AnonymousAuthenticationFilter::class.java)
        http.headers().cacheControl()
        // @formatter:on
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}