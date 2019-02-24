package org.codreaming.server.security

import org.codreaming.server.exception.ResourceNotFoundException
import org.codreaming.server.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
        @Autowired val userRepo: UserRepository
) : UserDetailsService {

    @Transactional
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepo.findByEmail(email) ?: throw  UsernameNotFoundException("User not found with email : $email")
        return UserPrincipal.create(user)
    }

    @Transactional
    fun loadUserById(id: Long): UserDetails {
        val user = userRepo.findByIdOrNull(id) ?: throw ResourceNotFoundException("User", "id", id)
        return UserPrincipal.create(user)
    }
}