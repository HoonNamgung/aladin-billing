package com.example.api.users.controller

import com.example.api.config.jwt.JwtTokenProvider
import com.example.api.users.dto.LoginRequest
import com.example.api.users.dto.SignUpRequest
import com.example.api.users.dto.UpdateUserRequest
import com.example.api.users.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest) =
        ResponseEntity.ok(userService.signUp(request))

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, String>> {
        val user = userService.login(request)
        val token = jwtTokenProvider.createToken(user.id)
        return ResponseEntity.ok(mapOf("access_token" to token))
    }

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal userId: Long) =
        ResponseEntity.ok(userService.getMyInfo(userId))

    @PutMapping("/me")
    fun update(@AuthenticationPrincipal userId: Long, @RequestBody request: UpdateUserRequest) =
        ResponseEntity.ok(userService.update(userId, request))

    @DeleteMapping("/me")
    fun delete(@AuthenticationPrincipal userId: Long): ResponseEntity<Void> {
        userService.delete(userId)
        return ResponseEntity.noContent().build()
    }
}
