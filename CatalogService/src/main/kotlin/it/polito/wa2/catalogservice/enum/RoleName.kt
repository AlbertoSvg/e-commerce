package it.polito.wa2.catalogservice.enum

enum class RoleName(val value: String) {
    // Attention, values must contain the "ROLE_" prefix
    ROLE_CUSTOMER("ROLE_CUSTOMER"),
    ROLE_ADMIN("ROLE_ADMIN")
}
