package io.github.sumfi.cron.heartbeat

import io.github.sumfi.cron.heartbeat.relocate.MessageRelocateService
import io.github.sumfi.support.log.logger
import io.github.sumfi.support.redis.operation.RedisOperation
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import kotlin.reflect.KClass

typealias Worker = String

@Service
class HeartBeatMonitorService(
	private val redisOperation: RedisOperation,
	private val messageRelocateServices: Map<KClass<*>, MessageRelocateService>,
) {
	companion object {
		const val HEART_BEAT_THRESHOLD = 60
		const val HEART_BEAT_TTL = 60L
	}

	private val clock = Clock.systemUTC()
	private val log = logger()

	@Scheduled(cron = "* * * * * *")
	fun forceRelocateTasksFromDeadWorkers() {
		val threshold = clock.instant().epochSecond - HEART_BEAT_THRESHOLD
		val deadWorkers = getDeadWorkers(threshold)
		log.info("dead worker relocate process is activating")
		deadWorkers.forEach { worker ->
			if (redisOperation.lockDeadWorker("RE:$worker", threshold, HEART_BEAT_TTL)) {
				log.info("relocate $worker tasks")
				forceRelocateTask(worker)
				redisOperation.eraseMessageMeta(worker)
				redisOperation.releaseDeadWorker("RE:$worker")
			}
		}
	}

	fun getDeadWorkers(threshold: Long): Set<Worker> {
		val heartBeatsTable = redisOperation.getEveryHeartBeatingNodes()
		return heartBeatsTable
			.filter { (_, beatTime) -> beatTime < threshold }
			.keys
	}

	fun forceRelocateTask(worker: Worker) {
		val metadatas = redisOperation.getEveryNodeMessageMetadata(worker)
		metadatas.forEach { messageMetadata ->
			val type = Class.forName(messageMetadata.type).kotlin
			val id = messageMetadata.id
			val relocator = messageRelocateServices[type]
			relocator?.relocate(id, worker) ?: log.error("enable to relocate TYPE [$type] ID [$id]")
		}
	}
}