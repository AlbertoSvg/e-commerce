package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.constants.Strings.EMAIL_ALREADY_EXISTS
import it.polito.wa2.catalogservice.constants.Strings.PASSWORD_DO_NOT_MATCH
import it.polito.wa2.catalogservice.constants.Strings.USERNAME_ALREADY_EXISTS
import it.polito.wa2.catalogservice.constants.Strings.USER_NOT_FOUND
import it.polito.wa2.catalogservice.dtos.*
import it.polito.wa2.catalogservice.enum.RoleName
import it.polito.wa2.catalogservice.security.JwtUtils
import it.polito.wa2.catalogservice.services.UserDetailsServiceImpl
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors
import javax.validation.Valid

@RestController
@RequestMapping("auth")
class AuthController() {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    @Autowired
    private lateinit var authenticationManager: ReactiveAuthenticationManager

    @Autowired
    private lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    suspend fun register(
        @Valid @RequestBody body: RegisterDTO
    ): ResponseEntity<Any> {
        // Check the correspondence between the password and the confirmation password
        if (body.password != body.confirmPassword)
            throw BadCredentialsException(PASSWORD_DO_NOT_MATCH)

        if (userDetailsServiceImpl.usernameExists(body.username).awaitSingle())
            throw BadCredentialsException(USERNAME_ALREADY_EXISTS)

        if (userDetailsServiceImpl.emailExists(body.email).awaitSingle())
            throw BadCredentialsException(EMAIL_ALREADY_EXISTS)

        // Save user in the db
        val user = userDetailsServiceImpl.registerUser(
            body.username,
            body.password,
            body.email,
            false,
            RoleName.ROLE_CUSTOMER.name,
            body.name,
            body.surname,
            body.address
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }


    @PostMapping("signin")
    @ResponseBody
    suspend fun signIn(
        @Valid @RequestBody body: LoginDTO
    ): ResponseEntity<Any> {
        val auth = UsernamePasswordAuthenticationToken(body.username, body.password)
        if (!userDetailsServiceImpl.usernameExists(body.username).awaitSingle()) {
            throw UsernameNotFoundException(USER_NOT_FOUND)
        }
        val authentication = authenticationManager.authenticate(auth).awaitSingle()
        ReactiveSecurityContextHolder.withAuthentication(authentication)
        val jwt = jwtUtils.generateJwtToken(authentication)
        val userDetails: UserDetailsDTO = authentication.principal as UserDetailsDTO
        val roles = userDetails.authorities.stream()
            .map { item: GrantedAuthority -> RoleName.valueOf(item.authority).value }
            .collect(Collectors.toSet())

        return ResponseEntity.ok().body(
            JwtResponseDTO(
                userDetails.getId(),
                userDetails.username,
                userDetails.getEmail(),
                jwt,
                roles
            )
        )
    }

    @GetMapping("registrationConfirm")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun registrationConfirm(
        @RequestParam(name = "token", defaultValue = "") token: String
    ): ResponseEntity<Any> {
        println("AAAAAAAAA")
        userDetailsServiceImpl.confirmUserRegistration(token)
        return ResponseEntity.accepted().body("Successfully registered...Redirect: /auth/signin")
    }

    @PostMapping("enableUser")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun enableUser(
        @Valid @RequestBody body: EnableUserDTO
    ): ResponseEntity<Any> {
        userDetailsServiceImpl.enableUser(body.username, body.enable)

        if (body.enable)
            return ResponseEntity.ok().body("USER ENABLED")
        else
            return ResponseEntity.ok().body("USER DISABLED")

    }

    @PostMapping("addRole")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun addRoleName(
        @Valid @RequestBody body: RoleNameToUserDTO
    ): ResponseEntity<Any> {
        userDetailsServiceImpl.addRoleToUser(body.username, body.role)
        return ResponseEntity.ok().body("Role added to ${body.username}")
    }

    @PostMapping("removeRole")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    suspend fun removeRoleName(
        @Valid @RequestBody body: RoleNameToUserDTO
    ): ResponseEntity<Any> {
        userDetailsServiceImpl.removeRoleFromUser(body.username, body.role)
        return ResponseEntity.ok().body("Role removed from ${body.username}")
    }
}
