package io.github.sumfi.worker.config

import io.github.sumfi.worker.heartbeat.HeartBeatService
import io.github.sumfi.worker.scheduler.SpringSchedulerHolder
import io.github.sumfi.worker.scheduler.TimingWheel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger

@Configuration
internal class SchedulerConfig {
    @Bean
    fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 60
        scheduler.setThreadNamePrefix("timing-wheel-scheduler-")

        return scheduler
    }
    @Bean
    fun timingWheel(): TimingWheel = TimingWheel()

    @Bean
    fun springSchedulerHolder(taskScheduler: TaskScheduler, timingWheel: TimingWheel, heartBeatService: HeartBeatService): SpringSchedulerHolder {
        val timingWheelCronTrigger = CronTrigger("* * * * * *")
        val timingWheelFutureTask = taskScheduler.schedule({ timingWheel.run() }, timingWheelCronTrigger)!!

        val heartBeatCronTrigger = CronTrigger("*/15 * * * * *")
        val heartBeatFutureTask = taskScheduler.schedule({ heartBeatService.logHeartBeat() }, heartBeatCronTrigger)!!
        return SpringSchedulerHolder(listOf(timingWheelFutureTask, heartBeatFutureTask))
    }
}