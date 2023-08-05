package io.github.sumfi.support.scylla.domain

import lombok.With
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

@Table("message")
@With
class SimpleIntMessage(
	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED) val id: UUID = UUID.randomUUID(),
	val intPayload: Int = 0,
	runAt: LocalDateTime = LocalDateTime.now()
) : SimpleMessage(runAt) {
	private var state: State = State.RESERVED
		private set
	var retryCount: Int = 0
		private set
	var retryLimit: Int = 5
		private set
	var retryInterval: Long = 1000
		private set

	fun tryRetry(): Boolean {
		return when (retryCount + 1 <= retryLimit) {
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