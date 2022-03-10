package io.dereknelson.lostcities.matches.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.dereknelson.lostcities.common.model.match.UserPair
import io.dereknelson.lostcities.matches.persistence.MatchEntity
import io.dereknelson.lostcities.matches.persistence.MatchRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.Optional

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

    private val user1 = "USER_1"
    private val user2 = "USER_2"


    @Test
    fun `concede is successful if not already conceded`() {
        val match = mock(MatchEntity::class.java)
        `when`(match.hasPlayer(user1)).thenReturn(true)
        `when`(match.concededBy).thenReturn(null)
        `when`(matchRepository.save(match)).thenReturn(match)

        matchService.concede(match, user1)

        verify(match).concededBy = user1
    }

    @Test
    fun `concede fails if a user outside of the game concedes`() {
        val match = mock(MatchEntity::class.java)

        assertThrows(RuntimeException::class.java) {
            matchService.concede(match, "UNKNOWN_USER")
        }
    }

    @Test
    fun finishGame() {
        val match = mock(MatchEntity::class.java)
        `when`(match.player1).thenReturn(user1)
        `when`(match.player2).thenReturn(user2)

        `when`(matchRepository.findById(1)).thenReturn(Optional.of(match))

        matchService.finishGame(
            1,
            LocalDateTime.MIN,
            mapOf(user1 to 20, user2 to 15)
        )

        verify(match, times(1)).let {
            it.isCompleted = true
            it.finishedAt = LocalDateTime.MIN
            it.score1 = 20
            it.score2 = 15
        }

        verify(matchRepository).save(match)
    }

    @Test
    fun findById() {
        matchService.findById(1)
        verify(matchRepository).findById(1)
    }

    @Test
    fun joinMatch() {
        val match = mock(MatchEntity::class.java)
        `when`(match.player2).thenReturn(null)
        `when`(matchRepository.save(match)).thenReturn(match)

        matchService.joinMatch(match, user2)

        verify(matchRepository).save(match)

        verify(match).let {
            it.player2 = user2
            it.isReady = true
        }

    }
}
