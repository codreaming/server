package org.codreaming.server.security.oauth2.user

abstract class OAuth2UserInfo(
        protected val attributes: MutableMap<String, Any>
) {
    abstract fun getId(): String
    abstract fun getName(): String
    abstract fun getEmail(): String
    abstract fun getImageUrl(): String
}