package io.dereknelson.lostcities.matches.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import java.util.Locale

@Configuration
class LocaleConfiguration : WebMvcConfigurer {
    @Bean
    fun localeResolver(): LocaleResolver {
        val cookieLocaleResolver = CookieLocaleResolver("LANG_KEY")
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH)
        return cookieLocaleResolver
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        val localeChangeInterceptor = LocaleChangeInterceptor()
        localeChangeInterceptor.paramName = "language"
        registry.addInterceptor(localeChangeInterceptor)
    }
}
