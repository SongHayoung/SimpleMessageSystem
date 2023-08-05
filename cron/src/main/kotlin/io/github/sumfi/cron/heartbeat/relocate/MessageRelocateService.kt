package io.github.sumfi.cron.heartbeat.relocate

import io.github.sumfi.cron.heartbeat.Worker
import kotlin.reflect.KClass

interface MessageRelocateService {
    fun relocate(id: String, worker: Worker)
    fun handlingType(): KClass<*>
}