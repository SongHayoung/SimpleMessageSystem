package io.github.sumfi.worker.scheduler

import kotlinx.coroutines.Job


class TimingWheelHolder(private val job: Job): Job by job {
}