package io.github.sumfi.support.domain

import java.time.LocalDateTime

data class SimpleLog(val message: String, val produceAt: LocalDateTime = LocalDateTime.now())