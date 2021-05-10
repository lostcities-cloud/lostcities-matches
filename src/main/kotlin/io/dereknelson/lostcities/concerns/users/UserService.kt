package io.dereknelson.lostcities.concerns.users

import io.dereknelson.lostcities.concerns.users.entity.UserEntity
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService {

    @Autowired
    private lateinit var modelMapper: ModelMapper

    @Autowired
    private lateinit var userRepository : UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    fun find(userDetails: UserDetails) : Optional<User> {
        return userRepository.findOneByLogin(userDetails.username)
            .map { modelMapper.map(it, User::class.java) }
    }

    fun findById(id: Long): Optional<User> {
        return userRepository.findById(id)
            .map { User(id=it.id, login=it.login!!, email=it.email!!, langKey=it.langKey ?: "en_US") }

    }

    fun findAllById(id: Iterable<Long>): Collection<User> {
        return userRepository.findAllById(id).map { modelMapper.map(it, User::class.java) }
    }

    fun delete(user: User) {
        userRepository.findById(user.id!!).ifPresent { userRepository.delete(it) }
    }

    fun register(registration: Registration): User {
        registration.password = passwordEncoder.encode(registration.password)

        var userEntity = modelMapper.map(
            registration,
            UserEntity::class.java
        )

        userEntity = userRepository.save(userEntity)

        return User(
            id=userEntity.id,
            login=userEntity.login!!,
            email=userEntity.email!!
        )
    }
}