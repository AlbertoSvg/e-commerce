package it.polito.wa2.saga.utils

fun String.parseID(exception:Exception?=null): Long {
    return this.toLongOrNull() ?: throw exception ?: RuntimeException("Invalid ID $this")
}