package io.dereknelson.lostcities.matches.archive

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchArchiveRepository : JpaRepository<MatchArchiveEntity, Long>
