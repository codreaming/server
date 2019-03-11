package org.codreaming.server.web

import org.codreaming.server.domain.AuthProvider
import org.codreaming.server.domain.User
import org.codreaming.server.exception.BadRequestException
import org.codreaming.server.repository.UserRepository
import org.codreaming.server.security.TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank


@RestController
@RequestMapping("/auth")
class AuthController(
        @Autowired val authenticationManager: AuthenticationManager,
        @Autowired val userRepo: UserRepository,
        @Autowired val passwordEncoder: PasswordEncoder,
        @Autowired val tokenProvider: TokenProvider
) {

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*> {

        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.email,
                        loginRequest.password
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val token = tokenProvider.createToken(authentication)
        return ResponseEntity.ok<Any>(AuthResponse(accessToken = token))
    }

    @PostMapping("/signUp")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<*> {
        if (userRepo.existsByEmail(signUpRequest.email)) {
            throw BadRequestException("Email address already in use.")
        }

        val user = User(
                signUpRequest.name,
                signUpRequest.email,
                provider = AuthProvider.LOCAL
        )
        user.password = passwordEncoder.encode(signUpRequest.password)
        val result = userRepo.save(user)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.id).toUri()
        return ResponseEntity.created(location)
                .body<Any>(ApiResponse(true, "User registered successfully"))
    }
}

class ApiResponse(val success: Boolean, val message: String)
class AuthResponse(val accessToken: String, val tokenType: String = "Bearer")
class LoginRequest(
        @NotBlank
        @Email
        var email: String,

        @NotBlank
        var password: String
)

class SignUpRequest(
        @NotBlank
        var name: String,

        @NotBlank
        @Email
        var email: String,

        @NotBlank
        var password: String
)