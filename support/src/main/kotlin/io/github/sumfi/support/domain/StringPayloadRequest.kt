package io.github.sumfi.support.domain

import java.time.LocalDateTime

data class StringPayloadRequest(
	val payload: String = "",
	val reserveAt: LocalDateTime = LocalDateTime.now()
)