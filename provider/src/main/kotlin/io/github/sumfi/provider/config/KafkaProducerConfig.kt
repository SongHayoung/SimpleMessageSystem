package io.github.sumfi.provider.config

import io.github.sumfi.support.domain.IntPayloadRequest
import io.github.sumfi.support.domain.StringPayloadRequest
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(
	@Value("\${spring.kafka.bootstrap-servers}") val bootstrapAddress: String
) {
	@Bean
	fun stringPayloadRequestProducerFactory(): ProducerFactory<String, StringPayloadRequest> {
		val configProps = buildMap<String, Any>(3) {
			put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress)
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer::class.java)
		}
		return DefaultKafkaProducerFactory(configProps)
	}

	@Bean
	fun stringPayloadKafkaTemplate(stringPayloadRequestProducerFactory: ProducerFactory<String, StringPayloadRequest>): KafkaTemplate<String, StringPayloadRequest> {
		return KafkaTemplate(stringPayloadRequestProducerFactory)
	}

	@Bean
	fun intPayloadRequestProducerFactory(): ProducerFactory<String, IntPayloadRequest> {
		val configProps = buildMap<String, Any>(3) {
			put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress)
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer::class.java)
		}
		return DefaultKafkaProducerFactory(configProps)
	}

	@Bean
	fun intPayloadKafkaTemplate(intPayloadRequestProducerFactory: ProducerFactory<String, IntPayloadRequest>): KafkaTemplate<String, IntPayloadRequest> {
		return KafkaTemplate(intPayloadRequestProducerFactory)
	}
}