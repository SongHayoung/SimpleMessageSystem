package io.github.sumfi.support.redis.config

import io.github.sumfi.support.redis.property.RedisProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableConfigurationProperties(RedisProperty::class)
class RedisConfig(
	val redisProperty: RedisProperty
) {
	@Bean
	fun redisConnectionFactory(): RedisConnectionFactory {
		val redisConfiguration = RedisStandaloneConfiguration()
		redisConfiguration.hostName = redisProperty.host
		redisConfiguration.port = redisProperty.port
		return LettuceConnectionFactory(redisConfiguration);
	}

	@Bean
	fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<*, *> {
		val redisTemplate = RedisTemplate<ByteArray, ByteArray>()
		redisTemplate.connectionFactory = redisConnectionFactory
		redisTemplate.keySerializer = StringRedisSerializer()
		redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()

		return redisTemplate
	}
}