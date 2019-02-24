package org.codreaming.server.security.oauth2

import org.codreaming.server.util.CookieUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class OAuth2AuthenticationFailureHandler(
        @Autowired val authorizationRequestRepo: HttpCookieOAuth2AuthorizationRequestRepository
) : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse,
                                         exception: AuthenticationException) {
        var targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)?.value ?: "/"

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString()

        authorizationRequestRepo.removeAuthorizationRequestCookies(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}