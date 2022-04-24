package it.polito.wa2.catalogservice.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import it.polito.wa2.catalogservice.configurations.JwtConfiguration
import it.polito.wa2.catalogservice.dtos.UserDetailsDTO
import it.polito.wa2.catalogservice.enum.RoleName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.time.Instant
import java.util.*
import java.util.stream.Collectors

@Component
class JwtUtils(private val jwtCfg: JwtConfiguration) {

    val logger: Logger = LoggerFactory.getLogger(JwtUtils::class.java)

    fun generateJwtToken(authentication: Authentication): String {
        val userDetailsDTO: UserDetailsDTO = authentication.principal as UserDetailsDTO
        val authorities: String = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))

        return Jwts.builder()
            .setIssuer("ecommerce")                               // Who created and signed this token
            .setId(userDetailsDTO.getId())
            .setSubject(userDetailsDTO.username)                // Whom the token refers to
            .claim("scope", authorities)
            .setIssuedAt(Date())
            .setExpiration(java.sql.Date.from(Instant.now().plusMillis(jwtCfg.expirationMs.toLong())))
            .signWith(
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtCfg.secret)),
                SignatureAlgorithm.HS256
            )
            .compact()
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtCfg.secret)
                .build()
                .parse(authToken)
            return true
        } catch (e: SecurityException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: SignatureException) {
            logger.error("JWT token signature exception: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT token illegal argument: {}", e.message)
        }
        return false
    }

    fun getDetailsFromJwtToken(authToken: String): UserDetailsDTO? {
        try {
            val body = Jwts.parserBuilder().setSigningKey(jwtCfg.secret).build().parseClaimsJws(authToken).body

            val id = body.id
            val username = body.subject
            val authorities: Set<RoleName> = (body["scope"].toString().split(",")).toSet()
                //.map { role: String? -> if (role == RoleName.ROLE_ADMIN.value) RoleName.ROLE_ADMIN else RoleName.ROLE_CUSTOMER }
                .map { role: String? ->
                     RoleName.valueOf(role!!)
                }
                .toSet()

            return UserDetailsDTO(
                id = id,
                username = username,
                roles = authorities
            )

        } catch (e: SecurityException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: SignatureException) {
            logger.error("JWT token signature: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT token illegal argument: {}", e.message)
        }
        return null
    }

    fun parseJwt(request: ServerHttpRequest): String? {
        val headerAuth = request.headers.getFirst("Authorization")
        if (headerAuth != null) {
            return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
                headerAuth.substring(7, headerAuth.length)
            } else null
        }
        return null
    }

}
