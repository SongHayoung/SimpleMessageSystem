package io.github.sumfi.worker.message.consumer

import io.github.sumfi.support.log.logger
import io.github.sumfi.support.scylla.domain.SimpleIntMessage
import io.github.sumfi.support.scylla.domain.State
import io.github.sumfi.support.scylla.repository.SimpleIntMessageRepository
import io.github.sumfi.worker.message.operator.SimpleIntMessageOperator
import io.github.sumfi.worker.scheduler.TimingWheel
import io.github.sumfi.worker.scheduler.Task
import org.springframework.stereotype.Component

@Component
internal class SimpleIntMessageListener(
	private val simpleIntMessageOperator: SimpleIntMessageOperator,
	private val simpleIntMessageRepository: SimpleIntMessageRepository,
	private val timingWheel: TimingWheel
) {
	companion object {
		const val THRESHOLD = 10
	}

	private val log = logger()

	fun handleMessage(runAfter: Long, simpleIntMessage: SimpleIntMessage) {
		log.info("message allocated ${simpleIntMessage.id} ")
		simpleIntMessage.updateState(State.ALLOCATED)
		simpleIntMessageRepository.save(simpleIntMessage)

		if (runAfter <= THRESHOLD) {
			simpleIntMessageOperator.operate(simpleIntMessage)
		} else {
			timingWheel.add(simpleIntMessage.runAt, Task(
				onEvent = { simpleIntMessageOperator.operate(simpleIntMessage) },
				onRelocate = { simpleIntMessageOperator.relocate(simpleIntMessage) }
			))
		}
	}
}