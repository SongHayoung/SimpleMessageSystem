package io.github.sumfi.worker.scheduler

import java.util.function.Consumer

internal class Task(val onEvent: Consumer<Unit>, val onRelocate: Consumer<Unit>){
}