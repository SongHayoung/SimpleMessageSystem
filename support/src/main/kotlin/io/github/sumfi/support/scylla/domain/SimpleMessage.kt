package io.github.sumfi.support.scylla.domain

import java.time.LocalDateTime

abstract class SimpleMessage(runAt: LocalDateTime) {
    var runAt: LocalDateTime = runAt
        protected set
}