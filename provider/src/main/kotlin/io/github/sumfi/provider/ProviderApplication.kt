package io.github.sumfi.provider

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication(scanBasePackages = ["io.github.sumfi"])
class ProviderApplication

fun main(args: Array<String>) {
	runApplication<ProviderApplication>(*args)
}
