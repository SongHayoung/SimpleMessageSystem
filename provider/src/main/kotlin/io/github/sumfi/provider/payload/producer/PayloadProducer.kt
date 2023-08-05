package io.github.sumfi.provider.payload.producer

import io.github.sumfi.support.domain.IntPayloadRequest
import io.github.sumfi.support.domain.StringPayloadRequest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PayloadProducer(
        private val stringPayloadKafkaTemplate: KafkaTemplate<String, StringPayloadRequest>,
        private val intPayloadKafkaTemplate: KafkaTemplate<String, IntPayloadRequest>,
) {
    companion object {
        const val STRING_TOPIC_NAME = "clerk-string"
        const val INT_TOPIC_NAME = "clerk-int"
    }
    fun sendMessage(payload: StringPayloadRequest) {
        stringPayloadKafkaTemplate.send(STRING_TOPIC_NAME, payload)
    }
    fun sendMessage(payload: IntPayloadRequest) {
        intPayloadKafkaTemplate.send(INT_TOPIC_NAME, payload)
    }
}