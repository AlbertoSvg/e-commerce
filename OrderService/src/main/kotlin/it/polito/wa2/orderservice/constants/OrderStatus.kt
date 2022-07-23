package it.polito.wa2.orderservice.constants

enum class OrderStatus {
    PENDING,
    ISSUED,
    DELIVERING,
    DELIVERED,
    FAILED,
    CANCELED
}