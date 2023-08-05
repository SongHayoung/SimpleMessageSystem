package io.github.sumfi.support.domain

import java.time.LocalDateTime

data class IntPayloadRequest(
	val payload: Int = 0,
	val reserveAt: LocalDateTime = LocalDateTime.now()
)