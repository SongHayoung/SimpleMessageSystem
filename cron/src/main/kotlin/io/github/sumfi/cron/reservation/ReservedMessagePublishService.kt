package io.github.sumfi.cron.reservation

import io.github.sumfi.support.constants.MessageTopic
import io.github.sumfi.support.log.logger
import io.github.sumfi.support.scylla.repository.SimpleIntMessageRepository
import io.github.sumfi.support.scylla.repository.SimpleStringMessageRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class ReservedMessagePublishService(
	private val simpleIntMessageRepository: SimpleIntMessageRepository,
	private val simpleStringMessageRepository: SimpleStringMessageRepository,
	private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
	private val clock = Clock.systemUTC()
	private val log = logger()

	@Scheduled(cron = "0 * * * * *")
	fun publishSimpleIntMessage() {
		val threshold = LocalDateTime.now(clock).plusMinutes(1)
		val messages = simpleIntMessageRepository.findAllByRunAtLessThanEqualAndState(threshold)
		messages.forEach { message ->
			log.info("publish simple int message ${message.id} ")
			kafkaTemplate.send(MessageTopic.WorkerMessageTopic, message)
		}
	}

	@Scheduled(cron = "0 * * * * *")
	fun publishSimpleStringMessage() {
		val threshold = LocalDateTime.now(clock).plusMinutes(1)
		val messages = simpleStringMessageRepository.findAllByRunAtLessThanEqualAndState(threshold)
		messages.forEach { message ->
			log.info("publish simple string message ${message.id} ")
			kafkaTemplate.send(MessageTopic.WorkerMessageTopic, message)
		}
	}
}