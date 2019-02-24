package org.codreaming.server.security.oauth2.user

import org.codreaming.server.domain.AuthProvider
import org.codreaming.server.exception.OAuth2AuthenticationProcessingException


object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(registrationId: String, attributes: MutableMap<String, Any>): OAuth2UserInfo {
        return if (registrationId.equals(AuthProvider.GOOGLE.toString(), ignoreCase = true)) {
            GoogleOAuth2UserInfo(attributes)
        } else {
            throw OAuth2AuthenticationProcessingException("Sorry! Login with $registrationId is not supported yet.")
        }
    }
}