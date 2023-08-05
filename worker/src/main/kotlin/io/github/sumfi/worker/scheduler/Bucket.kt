package io.github.sumfi.worker.scheduler

import java.util.*
import java.util.function.Consumer

internal class Bucket {
    private val simpleStringMessageTasks: MutableList<Task> = Collections.synchronizedList(mutableListOf())

    fun add(task: Task) {
        simpleStringMessageTasks.add(task)
    }

    private fun getTaskElements(extractor: (Task) -> Consumer<Unit>): List<Consumer<Unit>> {
        val result = mutableListOf<Consumer<Unit>>()
        while (simpleStringMessageTasks.isNotEmpty()) {
            val task = simpleStringMessageTasks.removeFirst()
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

    fun size(): Int = simpleStringMessageTasks.size
}