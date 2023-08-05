package io.github.sumfi.support.scylla.repository

import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import io.github.sumfi.support.scylla.domain.State
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface SimpleStringMessageRepository: CassandraRepository<SimpleStringMessage, UUID> {
    @AllowFiltering
    fun findAllByRunAtLessThanEqualAndState(threshold: LocalDateTime, state: State = State.RESERVED): List<SimpleStringMessage>
}