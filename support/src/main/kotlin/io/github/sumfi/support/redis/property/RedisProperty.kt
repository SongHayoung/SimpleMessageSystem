package io.github.sumfi.support.redis.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "spring.data.redis")
data class RedisProperty @ConstructorBinding constructor(
	val host: String,
	val port: Int,
)