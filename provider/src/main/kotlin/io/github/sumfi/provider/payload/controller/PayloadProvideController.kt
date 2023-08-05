package io.github.sumfi.provider.payload.controller

import io.github.sumfi.provider.payload.producer.PayloadProducer
import io.github.sumfi.support.domain.IntPayloadRequest
import io.github.sumfi.support.domain.StringPayloadRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PayloadProvideController (
        private val payloadProducer: PayloadProducer,
) {
    @PostMapping("/v1/string/payload/provide", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun provideStringRequest(@RequestBody request: StringPayloadRequest): Boolean {
        payloadProducer.sendMessage(request)
        return true
    }

    @PostMapping("/v1/int/payload/provide", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun provideIntRequest(@RequestBody request: IntPayloadRequest): Boolean {
        payloadProducer.sendMessage(request)
        return true
    }
}