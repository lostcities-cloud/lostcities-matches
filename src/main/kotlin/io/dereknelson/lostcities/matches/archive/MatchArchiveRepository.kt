package io.dereknelson.lostcities.matches.archive

import io.dereknelson.lostcities.matches.Constants
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MatchArchiveRepository : JpaRepository<MatchArchiveEntity, Long> {

}
