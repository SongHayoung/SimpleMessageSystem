package io.github.sumfi.worker.message.operator

import io.github.sumfi.support.constants.MessageTopic
import io.github.sumfi.support.log.logger
import io.github.sumfi.support.redis.operation.RedisOperation
import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import io.github.sumfi.support.scylla.domain.State
import io.github.sumfi.support.scylla.repository.SimpleStringMessageRepository
import io.github.sumfi.worker.operator.BaseOperator
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import kotlin.random.Random
import kotlin.random.nextInt

@Component
internal class SimpleStringMessageOperator(
        private val stringKafkaTemplate: KafkaTemplate<String, String>,
        private val redisOperation: RedisOperation,
        private val simpleStringMessageRepository: SimpleStringMessageRepository,
        private val relocateKafkaTemplate: KafkaTemplate<String, Any>
) : BaseOperator<SimpleStringMessage>() {
    private val log = logger()

    companion object {
        const val TOPIC_NAME = "outerMessage-string"
    }

    override fun operate(simpleStringMessage: SimpleStringMessage) {
        val fail = samplingFailure()
        if (fail) {
            onFailure(simpleStringMessage)
        } else {
            stringKafkaTemplate.send(TOPIC_NAME, simpleStringMessage.stringPayload)
            onSuccess(simpleStringMessage)
        }
    }

    private fun samplingFailure(sample: Int = 100): Boolean {
        check(sample in 0..100) { "sample value must in range [0, 100]" }

        return Random.nextInt(IntRange(0, 100)) > sample
    }

    protected override fun onSuccess(simpleStringMessage: SimpleStringMessage) {
        log.info("${simpleStringMessage.id} succeed [${simpleStringMessage.retryCount} / ${simpleStringMessage.retryLimit}]")
        simpleStringMessage.updateState(State.SUCCEED)
        simpleStringMessageRepository.save(simpleStringMessage)
        redisOperation.unlock(simpleStringMessage.id.toString())
    }

    protected override fun onFailure(simpleStringMessage: SimpleStringMessage) {
        val canRetry = simpleStringMessage.tryRetry()
        log.error("${simpleStringMessage.id} failed [${simpleStringMessage.retryCount} / ${simpleStringMessage.retryLimit}] ")
        if (!canRetry) {
            log.error("${simpleStringMessage.id} failed too many times. Can not proceed retry")
        }
        simpleStringMessageRepository.save(simpleStringMessage)
        redisOperation.unlock(simpleStringMessage.id.toString())
    }

    override fun relocate(simpleStringMessage: SimpleStringMessage) {
        log.info( "relocate message [${simpleStringMessage.id}]" )
        relocateKafkaTemplate.send(MessageTopic.WorkerMessageTopic, simpleStringMessage)
    }
}