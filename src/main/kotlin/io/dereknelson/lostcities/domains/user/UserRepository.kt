package io.dereknelson.lostcities.domains.user

import io.dereknelson.lostcities.common.User
import io.dereknelson.lostcities.domains.user.entity.UserEntity
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    companion object {
        const val USERS_BY_LOGIN_CACHE: String = "usersByLogin"
        const val USERS_BY_EMAIL_CACHE = "usersByEmail"
    }

    fun findOneByActivationKey(activationKey: String?): Optional<UserEntity>

    fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime: Instant): List<User>

    fun findOneByResetKey(resetKey: String): Optional<User>

    fun findOneByEmailIgnoreCase(email: String): Optional<User>

    fun findOneByLogin(login: String): Optional<User>

    @EntityGraph(attributePaths = ["authorities"])
    @Cacheable(cacheNames = [USERS_BY_LOGIN_CACHE])
    fun findOneWithAuthoritiesByLogin(login: String?): Optional<UserEntity>

    @EntityGraph(attributePaths = ["authorities"])
    @Cacheable(cacheNames = [USERS_BY_EMAIL_CACHE])
    fun findOneWithAuthoritiesByEmailIgnoreCase(email: String?): Optional<UserEntity>

    fun findAllByLoginNot(pageable: Pageable?, login: String?): Page<UserEntity>

}