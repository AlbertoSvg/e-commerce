package it.polito.wa2.walletservice.utils

import it.polito.wa2.walletservice.enum.RoleName
import org.springframework.stereotype.Component

@Component
class Utils {
    fun isAuthorized(roles: String?, userId: String?, walletOwnerId: Long?): Boolean {
        return (roles != null && roles.contains(RoleName.ROLE_ADMIN.name)) || (walletOwnerId != null && userId != null && walletOwnerId == userId.toLong())
    }
}