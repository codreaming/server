package org.codreaming.server.security.oauth2

import org.codreaming.server.domain.AuthProvider
import org.codreaming.server.domain.User
import org.codreaming.server.exception.OAuth2AuthenticationProcessingException
import org.codreaming.server.repository.UserRepository
import org.codreaming.server.security.UserPrincipal
import org.codreaming.server.security.oauth2.user.OAuth2UserInfo
import org.codreaming.server.security.oauth2.user.OAuth2UserInfoFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service


@Service
class CustomOAuth2UserService(
        @Autowired val userRepo: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User)
        } catch (ex: AuthenticationException) {
            throw ex
        } catch (ex: Exception) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }
    }

    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.clientRegistration.registrationId,
                oAuth2User.attributes)
        if (oAuth2UserInfo.getEmail().isBlank()) {
            throw OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider")
        }
        var user = userRepo.findByEmail(oAuth2UserInfo.getEmail())
        user = if (user != null) {
            if (user.provider != AuthProvider.valueOf(
                            oAuth2UserRequest.clientRegistration.registrationId.toUpperCase())) {
                throw OAuth2AuthenticationProcessingException("Looks like you're signed up with ${user.provider}"
                        + " account. Please use your ${user.provider} account to login.")
            }
            updateExistingUser(user, oAuth2UserInfo)
        } else {
            registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }

        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): User {
        val user = User(
                oAuth2UserInfo.getName(),
                oAuth2UserInfo.getEmail(),
                oAuth2UserInfo.getImageUrl(),
                AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId.toUpperCase()),
                oAuth2UserInfo.getId()
        )
        return userRepo.save(user)
    }

    private fun updateExistingUser(existingUser: User, oAuth2UserInfo: OAuth2UserInfo): User {
        existingUser.name = oAuth2UserInfo.getName()
        existingUser.imageUrl = oAuth2UserInfo.getImageUrl()
        return userRepo.save(existingUser)
    }
}