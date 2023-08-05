package io.github.sumfi.clerk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@EntityScan(basePackages = ["io.github.sumfi"])
@EnableCassandraRepositories(basePackages = ["io.github.sumfi"])
@SpringBootApplication(scanBasePackages = ["io.github.sumfi"])
class ClerkApplication

fun main(args: Array<String>) {
    runApplication<ClerkApplication>(*args)
}