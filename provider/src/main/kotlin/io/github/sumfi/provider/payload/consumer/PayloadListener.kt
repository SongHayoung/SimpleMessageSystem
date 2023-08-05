package io.github.sumfi.provider.payload.consumer

import io.github.sumfi.support.log.logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PayloadListener {
	private val log = logger()

	@KafkaListener(topics = ["outerMessage-string"], groupId = "outerMessage-string")
	fun handleStringMessage(payload: String) {
		log.info("string payload received $payload ")
	}

	@KafkaListener(topics = ["outerMessage-int"], groupId = "outerMessage-int")
	fun handleIntMessage(payload: Int) {
		log.info("int payload received $payload ")
	}
}