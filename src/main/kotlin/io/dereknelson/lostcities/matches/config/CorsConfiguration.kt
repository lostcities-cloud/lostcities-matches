package io.dereknelson.lostcities.matches.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfiguration {

    companion object {
        const val allowedOrigins = "*"
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()

        if (allowedOrigins.isNotEmpty()) {
            source.registerCorsConfiguration("/swagger-ui/**", CorsConfiguration())
            source.registerCorsConfiguration("/api/**", CorsConfiguration())
            source.registerCorsConfiguration("/management/**", CorsConfiguration())
            source.registerCorsConfiguration("/v3/api-docs", CorsConfiguration())
        }
        return CorsFilter(source)
    }
}