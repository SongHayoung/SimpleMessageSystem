package io.github.sumfi.worker.message.operator

import io.github.sumfi.support.constants.MessageTopic
import io.github.sumfi.support.log.logger
import io.github.sumfi.support.redis.operation.RedisOperation
import io.github.sumfi.support.scylla.domain.SimpleIntMessage
import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import io.github.sumfi.support.scylla.domain.State
import io.github.sumfi.support.scylla.repository.SimpleIntMessageRepository
import io.github.sumfi.worker.operator.BaseOperator
import io.github.sumfi.worker.util.RandomExceptionUtil
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.lang.Exception
import kotlin.random.Random
import kotlin.random.nextInt


@Component
internal class SimpleIntMessageOperator(
        private val intKafkaTemplate: KafkaTemplate<String, Int?>,
        private val redisOperation: RedisOperation,
        private val simpleIntMessageRepository: SimpleIntMessageRepository,
        private val relocateKafkaTemplate: KafkaTemplate<String, Any>
) : BaseOperator<SimpleIntMessage>() {
    private val log = logger()

    companion object {
        const val TOPIC_NAME = "outerMessage-int"
    }

    override fun operate(simpleIntMessage: SimpleIntMessage) {
		try {
			RandomExceptionUtil.samplingFailure(50)
			intKafkaTemplate.send(TOPIC_NAME, simpleIntMessage.intPayload)
			onSuccess(simpleIntMessage)
		} catch (ex: Exception) {
			log.error("${ex.message}", ex)
			onFailure(simpleIntMessage)
		}
    }

    protected override fun onSuccess(simpleIntMessage: SimpleIntMessage) {
        log.info("${simpleIntMessage.id} succeed [${simpleIntMessage.retryCount} / ${simpleIntMessage.retryLimit}]")
        simpleIntMessage.updateState(State.SUCCEED)
        simpleIntMessageRepository.save(simpleIntMessage)
        redisOperation.unlock(simpleIntMessage.id.toString())
    }

    protected override fun onFailure(simpleIntMessage: SimpleIntMessage) {
        val canRetry = simpleIntMessage.tryRetry()
        log.error("${simpleIntMessage.id} failed [${simpleIntMessage.retryCount} / ${simpleIntMessage.retryLimit}] ")
        if (!canRetry) {
            log.error("${simpleIntMessage.id} failed too many times. Can not proceed retry")
        }
        simpleIntMessageRepository.save(simpleIntMessage)
        redisOperation.unlock(simpleIntMessage.id.toString())
    }

    override fun relocate(simpleIntMessage: SimpleIntMessage) {
        log.info( "relocate message [${simpleIntMessage.id}]" )
        relocateKafkaTemplate.send(MessageTopic.WorkerMessageTopic, simpleIntMessage)
    }
}