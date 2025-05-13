// config/jwt/JwtAuthenticationToken.kt
package com.example.api.config.jwt

import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtAuthenticationToken(private val userId: Long) : AbstractAuthenticationToken(null) {
    override fun getCredentials() = null
    override fun getPrincipal() = userId

    override fun isAuthenticated(): Boolean = true
}
