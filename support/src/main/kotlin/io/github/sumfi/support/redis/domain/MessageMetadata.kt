package io.github.sumfi.support.redis.domain

import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

data class MessageMetadata(val type: String, val id: String) {
	constructor(kclazz: KClass<*>, id: String) : this(kclazz.jvmName, id) {
	}
}