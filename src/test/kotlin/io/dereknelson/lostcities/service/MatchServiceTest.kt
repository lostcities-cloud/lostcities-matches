import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.InjectMocks

internal class MatchServiceTest {
    
    @Mock
    val matchRepository: MatchRepository

    @InjectMocks
    val matchService: MatchService

    @Test
    fun markStarted() {
        
    }
}