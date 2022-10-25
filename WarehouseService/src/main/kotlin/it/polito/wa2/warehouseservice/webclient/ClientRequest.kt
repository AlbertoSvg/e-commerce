package it.polito.wa2.warehouseservice.webclient

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


@Component
class ClientRequest {

    @Autowired
    lateinit var webClientBuilder: WebClient.Builder

    fun <T> doGet(uri: String, className: Class<T>): T{
        val returnValue: T?

        try {
            returnValue = webClientBuilder.build()
                .get()
                .uri(uri)
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(className)
                .block()
        }
        catch (e: Exception){
            println(e.message)
            throw RuntimeException("${e.message}.\nError during connection with other server")
        }

        return returnValue ?: throw RuntimeException("No response from other server")
    }

}