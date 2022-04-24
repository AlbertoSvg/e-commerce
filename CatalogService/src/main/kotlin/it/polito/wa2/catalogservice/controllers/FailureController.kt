package it.polito.wa2.catalogservice.controllers

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
class FailureController {

    @GetMapping("/failure1")
    fun failure1(): String {
        return "Service1 is unavailable, try later"
    }

    @GetMapping("/failure2")
    fun failure2(): String {
        return "Service2 is unavailable, try later"
    }

}
