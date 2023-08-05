package io.github.sumfi.support.scylla.domain

enum class State {
    RESERVED,
    ALLOCATED,
    SUCCEED,
    FAILED,
    CANCELED
    ;
}