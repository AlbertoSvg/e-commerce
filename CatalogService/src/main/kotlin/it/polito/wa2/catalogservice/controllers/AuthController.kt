package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.costants.Strings.ACCOUNT_DISABLED
import it.polito.wa2.catalogservice.costants.Strings.EMAIL_ALREADY_EXISTS
import it.polito.wa2.catalogservice.costants.Strings.INCORRECT_PASSWORD
import it.polito.wa2.catalogservice.costants.Strings.PASSWORD_DO_NOT_MATCH
import it.polito.wa2.catalogservice.costants.Strings.USERNAME_ALREADY_EXISTS
import it.polito.wa2.catalogservice.costants.Strings.USER_NOT_FOUND
import it.polito.wa2.catalogservice.costants.Strings.WRONG_PARAMETERS
import it.polito.wa2.catalogservice.dtos.*
import it.polito.wa2.catalogservice.enum.RoleName
import it.polito.wa2.catalogservice.security.JwtUtils
import it.polito.wa2.catalogservice.services.implementations.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import java.util.stream.Collectors
import javax.validation.Valid


@RestController
@RequestMapping("auth")
class AuthController() {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    /**
     *  ### Description:
     *  Register a new user in the database.
     *  The username, a valid email, name, surname, address, password and confirmation password
     *  must be provided in the body of the request.
     *  After registration, an email will be sent containing the token used to verify the email address.
     *
     *  ### Mapping:
     *  /auth/register
     *
     * @param body must contain a valid RegisterDTO
     * @return UserDetailsDTO and status 201 (CREATED)
     */
    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    fun register(
        @Valid @RequestBody body: RegisterDTO,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.fieldError?.defaultMessage)
        } else {
            // Check the correspondence between the password and the confirmation password
            if (body.password != body.confirmPassword)
                return ResponseEntity.badRequest().body(PASSWORD_DO_NOT_MATCH)

            // Verify that the user is not already present in the database
            if (userDetailsServiceImpl.usernameExists(body.username)) {
                return ResponseEntity.badRequest().body(USERNAME_ALREADY_EXISTS)
            }

            // Verify that the email is not already present in the database
            if (userDetailsServiceImpl.emailExists(body.email))
                return ResponseEntity.badRequest().body(EMAIL_ALREADY_EXISTS)

            // Save user in the db
            val user = userDetailsServiceImpl.registerUser(
                body.username,
                body.password,
                body.email,
                false,
                RoleName.ROLE_CUSTOMER.value,
                body.name,
                body.surname,
                body.address
            )

            return ResponseEntity.status(HttpStatus.CREATED).body(user)
        }
    }

    /**
     *  ### Description:
     *  Through this endpoint it will be possible to log in and receive a jwt.
     *  A valid email and password are required.
     *
     *  ### Mapping:
     *  /auth/signin
     *
     * @param body must contain a valid LoginDTO
     * @return JwtResponseDTO and status 200 (OK)
     */
    @PostMapping("signin")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun signIn(
        @Valid @RequestBody body: LoginDTO,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.fieldError?.defaultMessage)
        } else {
            val auth = UsernamePasswordAuthenticationToken(body.username, body.password)
            try {
                if (!userDetailsServiceImpl.usernameExists(body.username)) {
                    return ResponseEntity.badRequest().body(USER_NOT_FOUND)
                }

                val authentication = authenticationManager.authenticate(auth)

                SecurityContextHolder.getContext().authentication = authentication
                val jwt = jwtUtils.generateJwtToken(authentication)

                val userDetails: UserDetailsDTO = authentication.principal as UserDetailsDTO
                val roles = userDetails.authorities.stream()
                    .map { item: GrantedAuthority -> item.authority }
                    .collect(Collectors.toSet())

                return ResponseEntity.ok(
                    JwtResponseDTO(
                        userDetails.username,
                        userDetails.getEmail(),
                        userDetails.getUserId(),
                        jwt,
                        roles
                    )
                )
            } catch (e: DisabledException) {
                return ResponseEntity.badRequest().body(ACCOUNT_DISABLED)
            } catch (e: BadCredentialsException) {
                return ResponseEntity.badRequest().body(INCORRECT_PASSWORD)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(e.message)
            }
        }
    }

    /**
     *  ### Description:
     *  Through this endpoint you can confirm your email address.
     *  It is necessary to be in possession of the UUID token sent during the registration phase.
     *  The token has a validity period.
     *  Periodically, expired tokens are cleaned up.
     *
     *  ### Mapping:
     *  /auth/registrationConfirm
     *
     * @param token as a parameter and must contain a valid Token sent by email
     * @return JwtResponseDTO and status 200 (OK)
     */
    @GetMapping("registrationConfirm")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun registrationConfirm(
        @RequestParam(name = "token", defaultValue = "") token: String
    ): ResponseEntity<Any> {
        return try {
            userDetailsServiceImpl.confirmUserRegistration(token)
            ResponseEntity.accepted().body("Successfully registered...Redirect: /auth/signin")
        } catch (e: BadCredentialsException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    /**
     *  ### Description:
     *  Through this endpoint it will be possible for an ADMIN to enable
     *  and disable a user
     *
     *  ### Mapping:
     *  /auth/enableUser
     *
     * @param body must contain a valid EnableUserDTO
     * @return "USER ENABLED" or "USER DISABLED" and status 200 (OK)
     */

    @PostMapping("enableUser")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun enableUser(
        @Valid @RequestBody body: EnableUserDTO,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(WRONG_PARAMETERS)
        } else {
            try {
                userDetailsServiceImpl.enableUser(body.username, body.enable)

                if (body.enable)
                    return ResponseEntity.ok().body("USER ENABLED")
                else
                    return ResponseEntity.ok().body("USER DISABLED")
            } catch (e: RuntimeException) {
                return ResponseEntity.badRequest().body(e.message)
            }
        }
    }
}
