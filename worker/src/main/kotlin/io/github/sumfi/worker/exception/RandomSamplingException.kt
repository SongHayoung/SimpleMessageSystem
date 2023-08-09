package io.github.sumfi.worker.exception

class RandomSamplingException: RuntimeException {
	constructor() : super()
	constructor(message: String) : super(message)
	constructor(message: String, cause: Throwable) : super(message, cause)
	constructor(cause: Throwable) : super(cause)
}