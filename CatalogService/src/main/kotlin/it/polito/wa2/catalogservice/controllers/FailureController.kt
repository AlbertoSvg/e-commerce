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

    @GetMapping("/failure")
    fun failure(): String {
        return "The service is unavailable, try later"
    }

}
