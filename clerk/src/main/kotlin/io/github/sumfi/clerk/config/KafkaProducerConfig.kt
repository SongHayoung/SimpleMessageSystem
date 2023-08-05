package io.github.sumfi.clerk.config

import io.github.sumfi.support.instance.property.ApplicationProperty
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(
	val applicationProperty: ApplicationProperty,
) {
	@Bean
	fun producerFactory(): ProducerFactory<String, Any> {
		val configProps = buildMap<String, Any>(4) {
			put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperty.bootstrapAddress)
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer::class.java)
			put(JsonSerializer.TYPE_MAPPINGS, applicationProperty.typeMappings)
		}
		return DefaultKafkaProducerFactory(configProps)
	}

	@Bean
	fun kafkaTemplate(producerFactory: ProducerFactory<String, Any>): KafkaTemplate<String, Any> {
		return KafkaTemplate(producerFactory)
	}
}