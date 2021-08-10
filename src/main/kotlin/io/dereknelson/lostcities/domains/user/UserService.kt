package io.dereknelson.lostcities.domains.user

import io.dereknelson.lostcities.common.model.User
import io.dereknelson.lostcities.domains.user.entity.UserEntity
import io.dereknelson.lostcities.common.Constants
import org.modelmapper.ModelMapper
import org.springframework.cache.CacheManager
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private var modelMapper: ModelMapper,
    private var userRepository : UserRepository,
    private var passwordEncoder: PasswordEncoder,
    private var cacheManager: CacheManager
) {
    fun find(userDetails: UserDetails) : Optional<User> {
        return userRepository.findOneByLogin(userDetails.username)
            .map { modelMapper.map(it, User::class.java) }
    }

    fun findById(id: Long): Optional<User> {
        return userRepository.findById(id)
            .map { User(id=it.id, login=it.login!!, email=it.email!!, langKey=it.langKey ?: Constants.DEFAULT_LANGUAGE) }

    }

    fun findAllById(id: Iterable<Long>): Collection<User> {
        return userRepository.findAllById(id).map { modelMapper.map(it, User::class.java) }
    }

    fun delete(user: User) {
        userRepository.findById(user.id!!).ifPresent { userRepository.delete(it) }
    }

    fun register(registration: Registration): User {
        registration.password = passwordEncoder.encode(registration.password)

        userRepository.findOneByLogin(registration.login).ifPresent {
            throw UserAlreadyExistsException("")
        }

        userRepository.findOneByEmailIgnoreCase(registration.email).ifPresent {
            throw UserAlreadyExistsException("")
        }

        var userEntity = modelMapper.map(
            registration,
            UserEntity::class.java
        )
        userEntity.activated = true
        userEntity = userRepository.save(userEntity)

        return User(
            id=userEntity.id,
            login=userEntity.login!!,
            email=userEntity.email!!
        )
    }

    fun activateRegistration(key: String?): Optional<User> {
        return userRepository
            .findOneByActivationKey(key)
            .map { user ->
                user.activated = true
                user.activationKey = null
                clearUserCaches(user)
                User(id=user.id, login=user.login!!, user.email!!, user.langKey!!)
            }
    }

    private fun clearUserCaches(user: UserEntity) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE))!!.evict(user.login!!)
        if (user.email != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE))!!.evict(user.email!!)
        }
    }
}