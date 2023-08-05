package io.github.sumfi.support.scylla.domain

import lombok.With
import lombok.experimental.Wither
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import kotlin.random.Random
import java.time.LocalDateTime
import java.util.*

@Table("message")
@With
class SimpleStringMessage(@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED) val id: UUID = UUID.randomUUID(), val stringPayload: String = "", runAt: LocalDateTime = LocalDateTime.now()): SimpleMessage(runAt) {
    private var state: State = State.RESERVED
        private set
    var retryCount: Int = 0
        private set
    var retryLimit: Int = 5
        private set
    var retryInterval: Long = 1000
        private set

    fun tryRetry(): Boolean {
        return when(retryCount + 1 <= retryLimit) {
            true -> {
                retryCount += 1
                updateState(State.RESERVED)
                updateRunAt()
                true
            }
            false -> {
                updateState(State.FAILED)
                false
            }
        }
    }

    fun updateState(state: State) {
        this.state = state
    }

    private fun updateRunAt() {
        runAt = LocalDateTime.now().plusSeconds(retryInterval + Random.nextInt(0, (retryInterval * 0.05).toInt()))
    }
}