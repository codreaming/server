package org.codreaming.server.security.oauth2

import org.codreaming.server.util.CookieUtils.addCookie
import org.codreaming.server.util.CookieUtils.deleteCookie
import org.codreaming.server.util.CookieUtils.deserialize
import org.codreaming.server.util.CookieUtils.getCookie
import org.codreaming.server.util.CookieUtils.serialize
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
const val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"

@Component
class HttpCookieOAuth2AuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private val cookieExpireSeconds = 180

    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        return getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)?.deserialize()
    }

    override fun saveAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest?,
                                          request: HttpServletRequest, response: HttpServletResponse) {
        if (authorizationRequest == null) {
            deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }

        addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                serialize(authorizationRequest), cookieExpireSeconds)
        val redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME)
        if (!redirectUriAfterLogin.isNullOrBlank()) {
            addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds)
        }
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        return this.loadAuthorizationRequest(request)
    }

    fun removeAuthorizationRequestCookies(request: HttpServletRequest, response: HttpServletResponse) {
        deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
    }
}