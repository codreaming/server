package org.codreaming.server.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import org.codreaming.server.config.AppProperties
import org.codreaming.server.logger
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.security.SignatureException
import java.util.Date


@Service
class TokenProvider(
        val appProps: AppProperties
) {
    companion object {
        val log by logger()
    }

    fun createToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date(now.time + appProps.auth.tokenExpirationMsec)

        return Jwts.builder()
                .setSubject(userPrincipal.id.toString())
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProps.auth.tokenSecret)
                .compact()
    }

    fun getUserIdFromToken(token: String) = Jwts.parser()
            .setSigningKey(appProps.auth.tokenSecret)
            .parseClaimsJws(token).body.subject.toLong()


    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(appProps.auth.tokenSecret).parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            log.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            log.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            log.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            log.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            log.error("JWT claims string is empty.")
        }
        return false
    }
}