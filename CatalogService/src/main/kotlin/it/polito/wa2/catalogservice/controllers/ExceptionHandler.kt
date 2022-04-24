package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.constants.Strings.ACCOUNT_DISABLED
import it.polito.wa2.catalogservice.dtos.ErrorMessageDTO
import org.springframework.cloud.gateway.support.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono
import java.util.stream.Collectors


@RestControllerAdvice
class ExceptionHandler {

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidation(e: WebExchangeBindException): Mono<ErrorMessageDTO>{
        val errors = e.bindingResult
                .allErrors
                .stream()
                .map { obj: ObjectError -> obj.defaultMessage }
                .collect(Collectors.toList()).joinToString(", ")
        return Mono.just(ErrorMessageDTO(errors, HttpStatus.BAD_REQUEST))
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialExceptionException(e : BadCredentialsException) : Mono<ErrorMessageDTO> {
        return Mono.just(ErrorMessageDTO(e.message, HttpStatus.BAD_REQUEST))
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DisabledException::class)
    fun handleDisabledException(e: DisabledException) : Mono<ErrorMessageDTO>{
        return Mono.just(ErrorMessageDTO(ACCOUNT_DISABLED, HttpStatus.BAD_REQUEST))
    }


    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(value=[NotFoundException::class, UsernameNotFoundException::class])
    fun handleNotFoundException(e: Exception) : Mono<ErrorMessageDTO>{
        return Mono.just(ErrorMessageDTO(e.message, HttpStatus.NOT_FOUND))
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleException( e: Exception): Mono<ErrorMessageDTO>{
        return Mono.just(ErrorMessageDTO(e.message, HttpStatus.INTERNAL_SERVER_ERROR))
    }
}