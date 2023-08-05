package io.github.sumfi.worker.listener

import io.github.sumfi.support.log.logger
import io.github.sumfi.worker.heartbeat.HeartBeatService
import io.github.sumfi.worker.scheduler.SpringSchedulerHolder
import io.github.sumfi.worker.scheduler.TimingWheel
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.stereotype.Component


@Component
internal class GracefulShutdownEventListener(
	private val springSchedulerHolder: SpringSchedulerHolder,
	private val timingWheel: TimingWheel,
	private val heartBeatService: HeartBeatService,
	private val kafkaListenerEndpointRegistry: KafkaListenerEndpointRegistry
) : ApplicationListener<ContextClosedEvent?> {
	private val log = logger()
	override fun onApplicationEvent(event: ContextClosedEvent) {
		val container = kafkaListenerEndpointRegistry.getListenerContainer("message")
		log.info("stop consume kafka event phase")
		container?.stop()
		log.info("stop timing wheel event phase")
		springSchedulerHolder.stop()
		log.info("relocate tasks phase")
		timingWheel.onGracefulShutdown()
		log.info("organize heart beat phase")
		heartBeatService.quitHeartBeat()
	}
}