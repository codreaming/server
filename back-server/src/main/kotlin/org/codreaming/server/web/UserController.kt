package org.codreaming.server.web

import org.codreaming.server.domain.User
import org.codreaming.server.exception.ResourceNotFoundException
import org.codreaming.server.repository.UserRepository
import org.codreaming.server.security.CurrentUser
import org.codreaming.server.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController(
        @Autowired val userRepo: UserRepository
) {

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser userPrincipal: UserPrincipal): User {
        val id = userPrincipal.id
        return if (id != null) {
            userRepo.findById(id).orElseThrow { ResourceNotFoundException("User", "id", userPrincipal.id) }
        } else {
            throw ResourceNotFoundException("User", "id", userPrincipal.id)
        }
    }
}