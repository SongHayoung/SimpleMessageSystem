package io.github.sumfi.worker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableScheduling

@EnableKafka
@EnableScheduling
@EntityScan(basePackages = ["io.github.sumfi"])
@EnableCassandraRepositories(basePackages = ["io.github.sumfi"])
@SpringBootApplication(scanBasePackages = ["io.github.sumfi"])
class WorkerApplication

fun main(args: Array<String>) {
    runApplication<WorkerApplication>(*args)
}
