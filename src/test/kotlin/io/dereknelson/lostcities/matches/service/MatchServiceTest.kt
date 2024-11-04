package io.dereknelson.lostcities.matches.service

import io.dereknelson.lostcities.matches.MatchService
import io.dereknelson.lostcities.matches.RankService
import io.dereknelson.lostcities.matches.match.MatchEntity
import io.dereknelson.lostcities.matches.match.MatchEventAmqpService
import io.dereknelson.lostcities.matches.match.MatchRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
internal class MatchServiceTest {

    @Mock
    lateinit var matchRepository: MatchRepository

    @Mock
    lateinit var rankService: RankService

    @Mock
    lateinit var eventAmqpService: MatchEventAmqpService

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
            mapOf(user1 to 20, user2 to 15),
        )

        verify(match, times(1)).isCompleted = true
        verify(match, times(1)).finishedAt = LocalDateTime.MIN
        verify(match, times(1)).score1 = 20
        verify(match, times(1)).score2 = 15

        verify(matchRepository).save(match)
    }

    @Test
    fun findById() {
        matchService.findById(1)
        verify(matchRepository).findById(1)
    }

    @Test
    fun joinMatch() {
        val match = MatchEntity(seed = 1L, player1 = "player1")
        `when`(matchRepository.save(any())).thenReturn(match)

        matchService.joinMatch(match, user2)

        verify(matchRepository).save(match)

        assertTrue(match.hasPlayer(user2))
        assertTrue(match.isReady)
    }
}
