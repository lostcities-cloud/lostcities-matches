package io.dereknelson.lostcities.matches.config

import io.dereknelson.lostcities.matches.library.cache.PrefixedKeyGenerator
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.jsr107.Eh107Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfiguration {
    companion object {
        var maxEntries: Long = 100
        var timeToLiveSeconds: Long = 3600
    }

    @Autowired(required = false)
    var gitProperties: GitProperties? = null

    @Autowired(required = false)
    var buildProperties: BuildProperties? = null

    private val jcacheConfiguration: javax.cache.configuration.Configuration<Any, Any> =
        Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Any::class.java, Any::class.java,
                ResourcePoolsBuilder.heap(maxEntries)
            )
                .withExpiry(
                    ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(timeToLiveSeconds))
                )
                .build()
        )

    val cacheManagerCustomizer: JCacheManagerCustomizer
        @Bean
        get() = JCacheManagerCustomizer { cm: javax.cache.CacheManager ->
            createCache(cm, MatchRepository.MATCHES_BY_PLAYER_CACHE)
        }

    val keyGenerator: KeyGenerator
        @Bean
        get() = PrefixedKeyGenerator(gitProperties, buildProperties)

    private fun createCache(cm: javax.cache.CacheManager, cacheName: String) {
        val cache = cm.getCache<Any, Any>(cacheName)
        if (cache == null) {
            cm.createCache(cacheName, jcacheConfiguration)
        }
    }
}
