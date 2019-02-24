package org.codreaming.server.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class AppProperties(
        val auth: Auth = Auth(),
        val oauth2: OAuth2 = OAuth2()
)

open class Auth {
    lateinit var tokenSecret: String
    var tokenExpirationMsec: Long = 0
}

class OAuth2 {
    var authorizedRedirectUris: MutableList<String> = mutableListOf()
}
