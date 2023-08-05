package io.github.sumfi.support.instance.config

import io.github.sumfi.support.instance.property.ApplicationProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InstanceConfig(
	@Value("\${spring.kafka.bootstrap-servers}") val bootstrapAddress: String,
	@Value("\${application.kafka.type-mappings}") val typeMappings: List<String>
) {
    @Bean
    fun applicationProperty(): ApplicationProperty = ApplicationProperty(System.getenv("HOSTNAME"), bootstrapAddress, typeMappings.joinToString(", "))
}