package io.github.sumfi.worker.heartbeat

import io.github.sumfi.support.instance.property.ApplicationProperty
import io.github.sumfi.support.log.logger
import io.github.sumfi.support.redis.operation.RedisOperation
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class HeartBeatService(
        private val applicationProperty: ApplicationProperty,
        private val redisOperation: RedisOperation,
) {
    private val clock = Clock.systemUTC()
    private val log = logger()

    fun logHeartBeat() {
        val epochSecond = clock.instant().epochSecond
        log.debug( "heart beating ${applicationProperty.hostName} at ${epochSecond}" )
        redisOperation.updateHeartBeat(applicationProperty.hostName, epochSecond)
    }

    fun quitHeartBeat() {
        redisOperation.eraseHeartBeat(applicationProperty.hostName)
    }
}