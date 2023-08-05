package io.github.sumfi.support.scylla.repository

import io.github.sumfi.support.scylla.domain.SimpleIntMessage
import io.github.sumfi.support.scylla.domain.State
import jdk.jfr.Threshold
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface SimpleIntMessageRepository: CassandraRepository<SimpleIntMessage, UUID> {
    @AllowFiltering
    fun findAllByRunAtLessThanEqualAndState(threshold: LocalDateTime, state: State = State.RESERVED): List<SimpleIntMessage>
}