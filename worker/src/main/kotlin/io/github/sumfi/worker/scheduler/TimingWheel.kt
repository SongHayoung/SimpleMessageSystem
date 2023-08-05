package io.github.sumfi.worker.scheduler

import io.github.sumfi.support.log.logger
import io.github.sumfi.support.scylla.domain.SimpleMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

internal class TimingWheel(
        private val bucketSize: Int = 60,
        private var cursor: Int = LocalDateTime.now().second % bucketSize) : TimerTask() {
    private val buckets: List<Bucket> = List(bucketSize) { Bucket() }
    private val clock = Clock.systemUTC()
    private val log = logger()

    override fun run() {
        cursor = (cursor + 1) % bucketSize
        executeBucketTask(cursor)
        if(cursor % 10 == 0) {
            syncCursorWitClock()
        }
    }

    private fun executeBucketTask(bucket: Int) {
        val tasks = buckets[bucket].getAllEventTasks()
        log.info( "run [${bucket}] tasks [${tasks.size}]" )
        runBlocking (Dispatchers.IO) {
            tasks.forEach { onEventFunction ->
                async { onEventFunction.accept(Unit) }
            }
        }
    }

    private fun syncCursorWitClock() {
        var previousCursorRunner = cursor
        cursor = clock.instant().epochSecond.toInt() % bucketSize
        if(cursor != previousCursorRunner) {
            log.info( "sync cursor from ${previousCursorRunner} to ${cursor} ")
            while(previousCursorRunner != cursor) {
                executeBucketTask(previousCursorRunner)
                previousCursorRunner = (previousCursorRunner + 1) % bucketSize
            }
            executeBucketTask(previousCursorRunner)
        }
    }

    fun add(runAt: LocalDateTime, task: Task) {
        val bucket = runAt.second % bucketSize
        buckets[bucket].add(task)
    }

    fun avg(): Double = buckets.map { bucket ->  bucket.size() }.average()

    fun max(): Int = buckets.maxOf { bucket -> bucket.size() }

    fun onGracefulShutdown() {
        val relocateTasks = buckets.map { bucket -> bucket.getAllRelocateTasks() }.flatten()
        log.info( "relocate tasks [${relocateTasks.size}]" )
        runBlocking (Dispatchers.IO) {
            relocateTasks.forEach { onRelocateFunction ->
                async { onRelocateFunction.accept(Unit) }
            }
        }
    }
}