package org.codreaming.server.security

import org.codreaming.server.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class UserPrincipal(
        val id: Long?,
        val email: String,
        @JvmField val password: String,
        @JvmField val authorities: MutableCollection<out GrantedAuthority>
) : OAuth2User, UserDetails {
    companion object {
        fun create(user: User): UserPrincipal {
            val authorities = mutableListOf<GrantedAuthority>(SimpleGrantedAuthority("ROLE_USER"))
            return UserPrincipal(user.id,
                    user.email,
                    user.password,
                    authorities)
        }

        fun create(user: User, attributes: MutableMap<String, Any>): UserPrincipal {
            val userPrincipal = create(user)
            userPrincipal.attributes = attributes
            return userPrincipal
        }
    }

    @JvmField
    var attributes: MutableMap<String, Any>? = null

    override fun getAuthorities() = authorities

    override fun getAttributes() = attributes

    override fun getPassword() = password

    override fun getName() = id.toString()

    override fun isEnabled() = true

    override fun getUsername() = email

    override fun isCredentialsNonExpired() = true

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true


}
