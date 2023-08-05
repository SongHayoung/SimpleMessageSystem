package io.github.sumfi.worker.scheduler

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer

internal class Bucket {
    private val simpleStringMessageTaskQueue: Queue<Task> = ConcurrentLinkedQueue<Task>()

    fun add(task: Task) {
        simpleStringMessageTaskQueue.add(task)
    }

    private fun getTaskElements(extractor: (Task) -> Consumer<Unit>): List<Consumer<Unit>> {
        val result = mutableListOf<Consumer<Unit>>()
        while (simpleStringMessageTaskQueue.isNotEmpty()) {
            val task = simpleStringMessageTaskQueue.poll()
            result.add(extractor.invoke(task))
        }

        return result
    }

    fun getAllEventTasks(): List<Consumer<Unit>> {
        return getTaskElements { task -> task.onEvent }
    }

    fun getAllRelocateTasks(): List<Consumer<Unit>>{
        return getTaskElements { task -> task.onRelocate }
    }

    fun size(): Int = simpleStringMessageTaskQueue.size
}