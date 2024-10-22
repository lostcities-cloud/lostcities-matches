package io.dereknelson.lostcities.matches

import io.dereknelson.lostcities.matches.rank.PlayerRankEntity
import io.dereknelson.lostcities.matches.rank.RankRepository
import org.springframework.stereotype.Component

@Component
class RankService(val rankRepository: RankRepository) {
    fun getPlayerRank(player: String): Int {
        val rank = rankRepository.findPlayerRankEntityByPlayer(player)

        if (rank.isEmpty) {
            val newRank = rankRepository.save(PlayerRankEntity(player = player))
            rankRepository.save(newRank)
            return newRank.rank
        }

        return rank.get().rank
    }
}
