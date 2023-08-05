package io.github.sumfi.worker.scheduler

import java.util.concurrent.ScheduledFuture

class SpringSchedulerHolder(val futureTasks: List<ScheduledFuture<*>>) {
    fun stop() {
        futureTasks.forEach { futureTask -> futureTask.cancel(true) }
    }
}