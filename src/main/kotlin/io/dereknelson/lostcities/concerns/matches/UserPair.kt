package io.dereknelson.lostcities.concerns.matches

import io.dereknelson.lostcities.concerns.users.User
import io.dereknelson.lostcities.concerns.users.UserRef
import java.util.*

data class UserPair (
    var user1: User?=null,
    var user2: User?=null,

    var score1: Int?=null,
    var score2: Int?=null
) {
    val isPopulated: Boolean
        get() = user1 != null && user2 != null

    fun contains(user: User): Boolean {
        return user1 == user || user2 == user
    }

    fun getPlayer(id: Long): User? {
        return if(user1?.id == id) {
            user1
        } else if(user2?.id == id) {
            user2
        } else {
            null
        }
    }

    fun shuffled(seed: Long) {
        if(isPopulated) {
            val users: List<User> = listOf(this.user1!!, this.user2!!)
                .shuffled(Random(seed))

            this.user1 = users[0]
            this.user2 = users[1]
        }
    }
}
