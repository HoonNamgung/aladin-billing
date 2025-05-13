package com.example.api.support

import com.example.api.config.jwt.JwtAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.test.context.support.WithSecurityContextFactory


class WithCustomMockUserSecurityContextFactory : WithSecurityContextFactory<WithCustomMockUser> {
    override fun createSecurityContext(annotation: WithCustomMockUser): SecurityContext {
        val userDetails: UserDetails = User(
            annotation.userId.toString(),
            "", // 패스워드는 빈 문자열로 설정
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val context: SecurityContext = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        return context
    }
}
