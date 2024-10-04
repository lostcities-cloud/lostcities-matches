package io.dereknelson.lostcities.matches.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfiguration {

    companion object {
        const val allowedOrigins = "http://192.168.1.241:8080, http://192.168.1.231:8091, 192.168.1.201:8091, *"
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()

        if (allowedOrigins.isNotEmpty()) {
            source.registerCorsConfiguration("/swagger-ui/**", corsConfiguration())
            source.registerCorsConfiguration("/api/matches/**", corsConfiguration())
            source.registerCorsConfiguration("/actuator/**", corsConfiguration())
            source.registerCorsConfiguration("/v3/api-docs", corsConfiguration())
        }

        return CorsFilter(source)
    }

    fun corsConfiguration(): CorsConfiguration {
        val config = CorsConfiguration()
        config.allowedOriginPatterns = mutableListOf(allowedOrigins)

        config.allowCredentials = true

        config.addAllowedMethod("OPTIONS")
        config.addAllowedMethod("HEAD")
        config.addAllowedMethod("GET")
        config.addAllowedMethod("PUT")
        config.addAllowedMethod("POST")
        config.addAllowedMethod("DELETE")
        config.addAllowedMethod("PATCH")

        return config
    }
}
