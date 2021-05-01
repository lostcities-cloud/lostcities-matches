package io.dereknelson.lostcities.concerns.users

import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService {
    @Autowired
    private lateinit var modelMapper: ModelMapper
    @Autowired
    private lateinit var userRepository : UserRepository

    fun findById(id: Long): Optional<User> {
        return userRepository.findById(id).map { modelMapper.map(it, User::class.java) }
    }

    fun findAllById(id: Iterable<Long>): Collection<User> {
        return userRepository.findAllById(id).map { modelMapper.map(it, User::class.java) }
    }

    fun delete(user: User) {
        userRepository.findById(user.id!!).ifPresent { userRepository.delete(it) }
    }
}