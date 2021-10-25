package io.dereknelson.lostcities.matches.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.dereknelson.lostcities.common.model.match.Match
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.Mock
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.Mockito.`when`
import org.springframework.amqp.rabbit.core.RabbitTemplate

import java.lang.RuntimeException


@ExtendWith(MockitoExtension::class)
internal class MatchServiceTest {
    
    @Mock
    lateinit var matchRepository: MatchRepository

    @Mock
    lateinit var rabbitTemplate: RabbitTemplate

    @Mock
    lateinit var objectMapper: ObjectMapper

    @InjectMocks
    lateinit var matchService: MatchService

    val user1 = "USER_1"
    val user2 = "USER_2"

    @Test
    fun `markStarted correctly starts a match`() {
        val match = Match(
            id = 1,
            seed = 1000,
            players = UserPair(
                user1 = user1,
                user2 = user2,
            ),
            isReady = true,
            isStarted = false
        )

        `when`(matchRepository.save(any()))
            .thenAnswer { it.getArgument(0) }

        val startedMatch = matchService.markStarted(match)

        assertThat(startedMatch.isStarted).isTrue
    }

    @Test
    fun `markStarted fails if match is already started`() {
        val match = Match(
            id = 1,
            seed = 1000,
            players = UserPair(
                user1 = user1,
                user2 = user2,
            ),
            isReady = true,
            isStarted = true
        )

        assertThrows(RuntimeException::class.java) {
            matchService.markStarted(match)
        }
    }

    @Test
    fun `markStarted fails if match is not ready`() {
        val match = Match(
            id = 1,
            seed = 1000,
            players = UserPair(
                user1 = user1,
                user2 = user2,
            ),
            isReady = false,
            isStarted = false
        )

        assertThrows(RuntimeException::class.java) {
            matchService.markStarted(match)
        }
    }

    @Test
    fun `markCompleted completes if not completed`() {
        val match = Match(
            id = 1,
            seed = 1000,
            players = UserPair(
                user1 = user1,
                user2 = user2,
            ),
            isReady = true,
            isStarted = true
        )

        `when`(matchRepository.save(any()))
            .thenAnswer { it.getArgument(0) }

        val completedMatch = matchService.markCompleted(match)

        assertThat(completedMatch.isCompleted).isTrue
    }

    @Test
    fun `markCompleted fails to complete if already completed`() {
        val match = Match(
            id = 1,
            seed = 1000,
            players = UserPair(
                user1 = user1,
                user2 = user2,
            ),
            isReady = true,
            isStarted = true,
            isCompleted = true
        )

        assertThrows(RuntimeException::class.java) {
            matchService.markCompleted(match)
        }
    }

    @Test
    fun `concede is successful if not already conceded`() {
        val match = Match(
            id = 1,
            seed = 1000,
            players = UserPair(
                user1 = user1,
                user2 = user2,
            ),
            isReady = true,
            isStarted = true,
            isCompleted = true
        )

        `when`(matchRepository.save(any()))
            .thenAnswer { it.getArgument(0) }

        val concededMatch = matchService.concede(match, user1)

        assertThat(concededMatch.concededBy).isEqualTo(user1)
    }

    @Test
    fun `concede fails if a user outside of the game concedes`() {
        val match = Match(
            id = 1,
            seed = 1000,
            players = UserPair(
                user1 = user1,
                user2 = user2,
            ),
            isReady = true,
            isStarted = true,
            isCompleted = true
        )

        assertThrows(RuntimeException::class.java) {
            matchService.concede(match, "UNKNOWN_USER")
        }
    }

    @Test
    fun findById() {
        matchService.findById(1)
        verify(matchRepository).findById(1)
    }

    @Test
    fun delete() {
        matchService.deleteById(1)
        verify(matchRepository).deleteById(1)
    }

    @Test
    fun joinMatch() {
        val match = Match(
            id = 1,
            seed = 1000,
            players = UserPair(
                user1 = user1,
                user2 = null,
            )
        )

        `when`(matchRepository.save(any()))
            .thenAnswer { it.getArgument(0) }

        val joinedMatch = matchService.joinMatch(match, user2)

        assertThat(joinedMatch.players.user2).isEqualTo(user2)
    }
}