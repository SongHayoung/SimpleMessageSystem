package io.github.sumfi.clerk.payload

import io.github.sumfi.clerk.message.MessageProducer
import io.github.sumfi.support.domain.IntPayloadRequest
import io.github.sumfi.support.domain.StringPayloadRequest
import io.github.sumfi.support.log.logger
import io.github.sumfi.support.scylla.domain.SimpleIntMessage
import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import io.github.sumfi.support.scylla.repository.SimpleIntMessageRepository
import io.github.sumfi.support.scylla.repository.SimpleStringMessageRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class PayloadListener(
	private val messageProducer: MessageProducer,
	private val simpleStringMessageRepository: SimpleStringMessageRepository,
	private val simpleIntMessageRepository: SimpleIntMessageRepository,
) {
	companion object {
		const val MESSAGE_THRESHOLD_SECONDS = 60
	}

	private val log = logger()

	@KafkaListener(topics = ["clerk-string"])
	fun handleStringPayloadRequest(stringPayloadRequest: StringPayloadRequest) {
		val simpleStringMessage = SimpleStringMessage(stringPayload = stringPayloadRequest.payload, runAt = stringPayloadRequest.reserveAt)
		simpleStringMessageRepository.save(simpleStringMessage)
		when (withInHurry(simpleStringMessage.runAt)) {
			true -> {
				log.info("send message [${simpleStringMessage.id}] immediately run at [${simpleStringMessage.runAt}]")
				messageProducer.sendMessage(simpleStringMessage)
			}

			false -> {
				log.info("message [${simpleStringMessage.id}] is not required to send cause run at [${simpleStringMessage.runAt}]")
			}
		}
	}

	@KafkaListener(topics = ["clerk-int"])
	fun handleIntPayloadRequest(intPayloadRequest: IntPayloadRequest) {
		val simpleIntMessage = SimpleIntMessage(intPayload = intPayloadRequest.payload, runAt = intPayloadRequest.reserveAt)
		simpleIntMessageRepository.save(simpleIntMessage)
		when (withInHurry(simpleIntMessage.runAt)) {
			true -> {
				log.info("send message [${simpleIntMessage.id}] immediately run at [${simpleIntMessage.runAt}]")
				messageProducer.sendMessage(simpleIntMessage)
			}

			false -> {
				log.info("message [${simpleIntMessage.id}] is not required to send cause run at [${simpleIntMessage.runAt}]")
			}
		}
	}

	private fun withInHurry(runAt: LocalDateTime): Boolean {
		return LocalDateTime.now().until(runAt, ChronoUnit.SECONDS) <= MESSAGE_THRESHOLD_SECONDS
	}
}