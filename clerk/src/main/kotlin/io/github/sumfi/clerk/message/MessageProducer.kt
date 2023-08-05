package io.github.sumfi.clerk.message

import io.github.sumfi.support.constants.MessageTopic
import io.github.sumfi.support.domain.SimpleLog
import io.github.sumfi.support.scylla.domain.SimpleIntMessage
import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class MessageProducer(
        private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
    fun sendMessage(simpleStringMessage: SimpleStringMessage) {
        kafkaTemplate.send(MessageTopic.WorkerMessageTopic, simpleStringMessage)
    }

    fun sendMessage(simpleIntMessage: SimpleIntMessage) {
        kafkaTemplate.send(MessageTopic.WorkerMessageTopic, simpleIntMessage)
    }

    private fun sendSimpleLog(message: String) {
        val simpleLog = SimpleLog(message)
        kafkaTemplate.send(MessageTopic.WorkerMessageTopic, simpleLog)
    }
}