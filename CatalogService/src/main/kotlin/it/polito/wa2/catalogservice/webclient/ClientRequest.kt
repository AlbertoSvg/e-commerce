package it.polito.wa2.catalogservice.webclient

import org.apache.http.HttpStatus
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono


@Component
class ClientRequest {

    @Autowired
    lateinit var webClientBuilder: WebClient.Builder

    fun <T: Any, R: Any> doPost(uri: String, requestBody: T, className: Class<T>, returnClassName: Class<R>) : Publisher<R>{
        val returnValue: Publisher<R>
        try {
            returnValue = webClientBuilder.build()
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody), className)
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(returnClassName)

        }
        catch (e: Exception){
            println(e.message)
            throw RuntimeException("Error during connection with other server")
        }

        return returnValue.switchIfEmpty (
                Mono.error(RuntimeException("No response from other server")))
    }

    fun <T> doGet(uri: String, className: Class<T>): T{
        val returnValue: T?

        try {
            returnValue = webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(className)
                .block()
        }
        catch (e: Exception){
            println(e.message)
            throw RuntimeException("Error during connection with other server")
        }

        return returnValue ?: throw RuntimeException("No response from other server")
    }

    fun <T> doGetReactive(uri: String, className: Class<T>, single: Boolean = false): Publisher<T> {
        val returnValue: Publisher<T>

        try {
            returnValue = if (single)
                webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(className)
            else
                webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(className)
        }
        catch (e: Exception){
            return Mono.error(RuntimeException("Error during connection with other server"))
        }


        return if (returnValue is Mono)
            returnValue.switchIfEmpty (
                Mono.error(RuntimeException("No response from other server")))
        else
            returnValue
    }
}