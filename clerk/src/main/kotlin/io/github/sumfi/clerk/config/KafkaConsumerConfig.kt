package io.github.sumfi.clerk.config

import io.github.sumfi.support.instance.property.ApplicationProperty
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig(
	val applicationProperty: ApplicationProperty,
) {

	@Bean
	fun consumerFactory(): ConsumerFactory<String, Any> {
		val props = buildMap<String, Any>(4) {
			put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperty.bootstrapAddress)
			put(ConsumerConfig.GROUP_ID_CONFIG, "clerk")
			put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
			put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer::class.java)
		}

		val jsonDeserializer = JsonDeserializer<Any>()
		jsonDeserializer.addTrustedPackages("io.github.sumfi.support.domain")

		return DefaultKafkaConsumerFactory(props, StringDeserializer(), jsonDeserializer)
	}

	@Bean
	fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, Any>): ConcurrentKafkaListenerContainerFactory<String, Any> {
		val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
		factory.consumerFactory = consumerFactory
		return factory
	}
}