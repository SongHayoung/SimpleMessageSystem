package io.github.sumfi.worker.message.consumer

import io.github.sumfi.support.log.logger
import io.github.sumfi.support.redis.domain.MessageMetadata
import io.github.sumfi.support.redis.operation.RedisOperation
import io.github.sumfi.support.scylla.domain.SimpleIntMessage
import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
@KafkaListener(topics = ["message"])
internal class MessageTopicListener(
	private val redisOperation: RedisOperation,
	private val simpleIntMessageListener: SimpleIntMessageListener,
	private val simpleStringMessageListener: SimpleStringMessageListener,
) {
	companion object {
		const val THRESHOLD = 10
	}

	private val log = logger()

	fun assignTask(taskId: String, messageMetadata: MessageMetadata, ttl: Long): Boolean {
		val assigned = redisOperation.lock(taskId, messageMetadata, ttl = ttl)
		if (!assigned) {
			log.info("drop message $taskId ")
		}
		return assigned
	}

	@KafkaHandler
	fun handleMessage(simpleStringMessage: SimpleStringMessage) {
		log.info("received simple string message ID[${simpleStringMessage.id}] PAYLOAD[${simpleStringMessage.stringPayload}} ")
		val runAfter = LocalDateTime.now().until(simpleStringMessage.runAt, ChronoUnit.SECONDS)
		val messageMetadata = MessageMetadata(SimpleStringMessage::class, simpleStringMessage.id.toString())
		if (!assignTask(simpleStringMessage.id.toString(), messageMetadata, runAfter + THRESHOLD)) {
			return
		}

		simpleStringMessageListener.handleMessage(runAfter, simpleStringMessage)
	}

	@KafkaHandler
	fun handleMessage(simpleIntMessage: SimpleIntMessage) {
		log.info("received simple int message ID[${simpleIntMessage.id}] PAYLOAD[${simpleIntMessage.intPayload}} ")
		val runAfter = LocalDateTime.now().until(simpleIntMessage.runAt, ChronoUnit.SECONDS)
		val messageMetadata = MessageMetadata(SimpleIntMessage::class, simpleIntMessage.id.toString())

		if (!assignTask(simpleIntMessage.id.toString(), messageMetadata, runAfter + THRESHOLD)) {
			return
		}

		simpleIntMessageListener.handleMessage(runAfter, simpleIntMessage)
	}
}