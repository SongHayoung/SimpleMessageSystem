package io.github.sumfi.worker.message.consumer

import io.github.sumfi.support.log.logger
import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import io.github.sumfi.support.scylla.domain.State
import io.github.sumfi.support.scylla.repository.SimpleStringMessageRepository
import io.github.sumfi.worker.message.operator.SimpleStringMessageOperator
import io.github.sumfi.worker.scheduler.TimingWheel
import io.github.sumfi.worker.scheduler.Task
import org.springframework.stereotype.Component

@Component
internal class SimpleStringMessageListener(
	private val simpleStringMessageOperator: SimpleStringMessageOperator,
	private val simpleStringMessageRepository: SimpleStringMessageRepository,
	private val timingWheel: TimingWheel
) {
	companion object {
		const val THRESHOLD = 10
	}

	private val log = logger()

	fun handleMessage(runAfter: Long, simpleStringMessage: SimpleStringMessage) {
		log.info("message allocated ${simpleStringMessage.id} ")
		simpleStringMessage.updateState(State.ALLOCATED)
		simpleStringMessageRepository.save(simpleStringMessage)

		if (runAfter <= THRESHOLD) {
			simpleStringMessageOperator.operate(simpleStringMessage)
		} else {
			timingWheel.add(simpleStringMessage.runAt, Task(
				onEvent = { simpleStringMessageOperator.operate(simpleStringMessage) },
				onRelocate = { simpleStringMessageOperator.relocate(simpleStringMessage) }
			))
		}
	}
}