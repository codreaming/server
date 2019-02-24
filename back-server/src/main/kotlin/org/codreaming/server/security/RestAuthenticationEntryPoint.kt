package org.codreaming.server.security

import org.codreaming.server.logger
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class RestAuthenticationEntryPoint : AuthenticationEntryPoint {
    companion object {
        val log by logger()
    }

    override fun commence(httpServletRequest: HttpServletRequest,
                          httpServletResponse: HttpServletResponse,
                          oO: AuthenticationException) {
        log.error("Responding with unauthorized error. Message - $oO")
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                oO.localizedMessage)
    }
}