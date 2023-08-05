package io.github.sumfi.support.redis.operation

import io.github.sumfi.support.instance.property.ApplicationProperty
import io.github.sumfi.support.redis.domain.MessageMetadata
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component

@Component
class RedisOperation(
	private val lock: RedisScript<Boolean>,
	private val unlock: RedisScript<Boolean>,
	private val forceUnlock: RedisScript<Boolean>,
	private val deadWorkerLock: RedisScript<Boolean>,
	private val redisTemplate: RedisTemplate<String, Long>,
	private val redisMetaDataTemplate: RedisTemplate<String, MessageMetadata>,
	private val applicationProperty: ApplicationProperty
) {
	companion object {
		const val HEART_BEAT = "SMS:HB"
	}

	fun lock(key: String, messageMetadata: MessageMetadata, ttl: Long): Boolean {
		val messageMetaKey = "MT:${applicationProperty.hostName}"
		return redisTemplate.execute(lock, listOf(key, messageMetaKey), applicationProperty.hostName, ttl, messageMetadata)
	}

	fun unlock(key: String): Boolean {
		val messageMetaKey = "MT:${applicationProperty.hostName}"
		return redisTemplate.execute(unlock, listOf(key, messageMetaKey))
	}

	fun forceUnlock(key: String, worker: String): Boolean {
		val messageMetaKey = "MT:$worker"
		return redisTemplate.execute(forceUnlock, listOf(key, messageMetaKey))
	}

	fun updateHeartBeat(hashKey: String, pulse: Long) {
		redisTemplate.opsForHash<String, Long>().put(HEART_BEAT, hashKey, pulse)
	}

	fun eraseHeartBeat(hashKey: String) {
		redisTemplate.opsForHash<String, Long>().delete(HEART_BEAT, hashKey)
	}

	fun eraseMessageMeta(key: String) {
		val messageMetaKey = "MT:$key"
		redisTemplate.delete(messageMetaKey)
	}

	fun getEveryHeartBeatingNodes(): Map<String, Long> {
		return redisTemplate.opsForHash<String, Long>().entries(HEART_BEAT)
	}

	fun lockDeadWorker(key: String, threshold: Long, ttl: Long): Boolean {
		return redisTemplate.execute(deadWorkerLock, listOf(key), threshold, ttl)
	}

	fun releaseDeadWorker(key: String): Boolean {
		return redisTemplate.unlink(key)
	}

	fun getEveryNodeMessageMetadata(key: String): List<MessageMetadata> {
		val messageMetaKey = "MT:$key"
		return redisMetaDataTemplate.opsForHash<String, MessageMetadata>().entries(messageMetaKey).values.toList()
	}
}