package io.github.sumfi.cron.heartbeat.relocate

import io.github.sumfi.cron.heartbeat.Worker
import io.github.sumfi.support.constants.MessageTopic
import io.github.sumfi.support.log.logger
import io.github.sumfi.support.redis.operation.RedisOperation
import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import io.github.sumfi.support.scylla.domain.State
import io.github.sumfi.support.scylla.repository.SimpleStringMessageRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.reflect.KClass

@Service
class SimpleStringMessageRelocateService(
	private val simpleStringMessageRepository: SimpleStringMessageRepository,
	private val kafkaTemplate: KafkaTemplate<String, Any>,
	private val redisOperation: RedisOperation,
) : MessageRelocateService {
	private val log = logger()
	override fun relocate(id: String, worker: Worker) {
		val message = simpleStringMessageRepository.findById(UUID.fromString(id))
		if (!message.isPresent) {
			log.error("can not find message $id")
		}
		message.ifPresent { message ->
			log.info("relocate simple string message ${message.id}")
			message.updateState(State.RESERVED)
			redisOperation.forceUnlock(id, worker)
			simpleStringMessageRepository.save(message)
			kafkaTemplate.send(MessageTopic.WorkerMessageTopic, message)
		}
	}

	override fun handlingType(): KClass<*> = SimpleStringMessage::class
}