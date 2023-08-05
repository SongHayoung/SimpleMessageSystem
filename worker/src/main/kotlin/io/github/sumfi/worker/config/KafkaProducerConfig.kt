package io.github.sumfi.worker.config

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
	fun stringProducerFactory(): ProducerFactory<String, String> {
		val configProps = buildMap<String, Any>(3) {
			put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperty.bootstrapAddress)
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer::class.java)
		}
		return DefaultKafkaProducerFactory(configProps)
	}

	@Bean
	fun stringKafkaTemplate(stringProducerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
		return KafkaTemplate(stringProducerFactory)
	}

	@Bean
	fun intProducerFactory(): ProducerFactory<String, Int> {
		val configProps = buildMap<String, Any>(3) {
			put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperty.bootstrapAddress)
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer::class.java)
		}
		return DefaultKafkaProducerFactory(configProps)
	}

	@Bean
	fun intKafkaTemplate(intProducerFactory: ProducerFactory<String, Int>): KafkaTemplate<String, Int> {
		return KafkaTemplate(intProducerFactory)
	}

	@Bean
	fun relocateProducerFactory(): ProducerFactory<String, Any> {
		val configProps = buildMap<String, Any>(4) {
			put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperty.bootstrapAddress)
			put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
			put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer::class.java)
			put(JsonSerializer.TYPE_MAPPINGS, applicationProperty.typeMappings)
		}
		return DefaultKafkaProducerFactory(configProps)
	}

	@Bean
	fun relocateKafkaTemplate(relocateProducerFactory: ProducerFactory<String, Any>): KafkaTemplate<String, Any> {
		return KafkaTemplate(relocateProducerFactory)
	}
}