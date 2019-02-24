package org.codreaming.server.util

import org.springframework.util.SerializationUtils
import java.util.Base64
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


object CookieUtils {

    fun getCookie(request: HttpServletRequest, name: String): Cookie? {
        val cookies = request.cookies

        if (cookies != null && cookies.isNotEmpty()) {
            return cookies.lastOrNull { it.name == name }
        }
        return null
    }

    fun addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int) {
        val cookie = Cookie(name, value).apply {
            path = "/"
            isHttpOnly = true
            this.maxAge = maxAge
        }
        response.addCookie(cookie)
    }

    fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
        val cookies = request.cookies
        if (cookies != null && cookies.isNotEmpty()) {
            cookies.forEach {
                if (it.name == name) {
                    it.value = ""
                    it.path = "/"
                    it.maxAge = 0
                    response.addCookie(it)
                }
            }
        }
    }

    fun serialize(obj: Any): String = Base64.getUrlEncoder()
            .encodeToString(SerializationUtils.serialize(obj))

    inline fun <reified T> Cookie.deserialize() = SerializationUtils.deserialize(
            Base64.getUrlDecoder().decode(this.value)) as T


}