package io.github.sumfi.support.redis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.script.RedisScript

@Configuration
class ScriptConfig {
    @Bean
    fun lock(): RedisScript<Boolean> {
        val script = ClassPathResource("redis/lua/lock.lua")
        return RedisScript.of(script, Boolean::class.java)
    }

    @Bean
    fun unlock(): RedisScript<Boolean> {
        val script = ClassPathResource("redis/lua/unlock.lua")
        return RedisScript.of(script, Boolean::class.java)
    }

    @Bean
    fun forceUnlock(): RedisScript<Boolean> {
        val script = ClassPathResource("redis/lua/force_unlock.lua")
        return RedisScript.of(script, Boolean::class.java)
    }

    @Bean
    fun deadWorkerLock(): RedisScript<Boolean> {
        val script = ClassPathResource("redis/lua/dead_worker_lock.lua")
        return RedisScript.of(script, Boolean::class.java)
    }
}