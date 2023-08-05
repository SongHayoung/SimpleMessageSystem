package io.github.sumfi.worker.config

import io.github.sumfi.support.constants.MessageTopic
import io.github.sumfi.support.domain.SimpleLog
import io.github.sumfi.support.instance.property.ApplicationProperty
import io.github.sumfi.support.scylla.domain.SimpleIntMessage
import io.github.sumfi.support.scylla.domain.SimpleStringMessage
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig(
	val applicationProperty: ApplicationProperty
) {
	@Bean
	fun typeMapper(): DefaultJackson2JavaTypeMapper {
		val typeMapper = DefaultJackson2JavaTypeMapper()
		typeMapper.typePrecedence = Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID
		typeMapper.addTrustedPackages("*")
		val mappings = buildMap<String, Class<*>>(3) {
			put("simpleStringMessage", SimpleStringMessage::class.java)
			put("simpleIntMessage", SimpleIntMessage::class.java)
			put("simpleLog", SimpleLog::class.java)
		}
		typeMapper.idClassMapping = mappings

		return typeMapper
	}

	@Bean
	fun converter(typeMapper: DefaultJackson2JavaTypeMapper): RecordMessageConverter {
		val converter = StringJsonMessageConverter()
		converter.typeMapper = typeMapper
		return converter
	}

	@Bean
	fun consumerFactory(typeMapper: DefaultJackson2JavaTypeMapper): ConsumerFactory<String, Any> {
		val props = buildMap<String, Any>(4) {
			put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperty.bootstrapAddress)
			put(ConsumerConfig.GROUP_ID_CONFIG, MessageTopic.WorkerMessageTopic)
			put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
			put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer::class.java)
		}

		val jsonDeserializer = JsonDeserializer<Any>()
		val jsonDeserializerProps = buildMap<String, Any>(1) {
			put(JsonDeserializer.TYPE_MAPPINGS, applicationProperty.typeMappings)
		}
		jsonDeserializer.configure(jsonDeserializerProps, false)
		return DefaultKafkaConsumerFactory(props, StringDeserializer(), jsonDeserializer)
	}

	@Bean
	fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, Any>, converter: RecordMessageConverter): ConcurrentKafkaListenerContainerFactory<String, Any> {
		val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
		factory.consumerFactory = consumerFactory
		factory.setRecordFilterStrategy { record: ConsumerRecord<String, Any> -> record.headers().lastHeader("SKIP") != null }

		return factory
	}
}