package com.example.api.users.controller

import com.example.api.config.jwt.JwtTokenProvider
import com.example.api.users.infra.google.client.GoogleHttpClient
import com.example.api.users.repository.UserRepository
import com.example.api.users.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/social")
class SocialLoginController(
    private val googleHttpClient: GoogleHttpClient,
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @GetMapping("/callback")
    fun googleCallback(@RequestParam code: String): ResponseEntity<Map<String, String>> {
        val accessToken = googleHttpClient.getAccessToken(code)
        val userInfo = googleHttpClient.getUserInfo(accessToken)

        val email = userInfo.email
        val nickname = userInfo.name

        val user = userRepository.findByEmail(email)
            ?: userRepository.save(User(email = email, password = "", nickname = nickname))

        val jwt = jwtTokenProvider.createToken(user.id)
        return ResponseEntity.ok(mapOf("access_token" to jwt))
    }
}
