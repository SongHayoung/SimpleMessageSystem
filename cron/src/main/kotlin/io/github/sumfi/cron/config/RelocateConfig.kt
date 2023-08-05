package io.github.sumfi.cron.config

import io.github.sumfi.cron.heartbeat.relocate.MessageRelocateService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KClass

@Configuration
class RelocateConfig {
	@Bean
	fun messageRelocateServices(
		messageRelocateServices: List<MessageRelocateService>,
	): Map<KClass<*>, MessageRelocateService> = buildMap {
		messageRelocateServices.forEach { messageRelocateService ->
			put(messageRelocateService.handlingType(), messageRelocateService)
		}
	}
}