package io.dereknelson.lostcities.matches.rank

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RankRepository : JpaRepository<PlayerRankEntity, Long> {
    // @Cacheable(cacheNames = [MATCHES_BY_PLAYER_CACHE])
    fun findPlayerRankEntityByPlayer(player: String): Optional<PlayerRankEntity>
}
