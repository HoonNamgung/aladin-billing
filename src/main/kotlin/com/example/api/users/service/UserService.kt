package com.example.api.users.service

import com.example.api.users.dto.LoginRequest
import com.example.api.users.dto.SignUpRequest
import com.example.api.users.dto.UpdateUserRequest
import com.example.api.users.dto.UserResponse
import com.example.api.users.model.User
import com.example.api.users.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    private val passwordEncoder = BCryptPasswordEncoder()

    fun signUp(request: SignUpRequest): UserResponse {
        require(userRepository.findByEmail(request.email) == null) { "Email already exists" }
        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            nickname = request.nickname
        )
        return userRepository.save(user).toResponse()
    }

    fun login(request: LoginRequest): User {
        val user = userRepository.findByEmail(request.email) ?: throw IllegalArgumentException("User not found")
        if (!passwordEncoder.matches(request.password, user.password)) throw IllegalArgumentException("Invalid password")
        return user
    }

    fun getMyInfo(userId: Long): UserResponse =
        userRepository.findById(userId).orElseThrow { NoSuchElementException("User not found") }.toResponse()

    fun update(userId: Long, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(userId).orElseThrow()
        request.password?.let { user.password = passwordEncoder.encode(it) }
        request.nickname?.let { user.nickname = it }
        return userRepository.save(user).toResponse()
    }

    fun delete(userId: Long) {
        userRepository.deleteById(userId)
    }

    private fun User.toResponse() = UserResponse(id, email, nickname)
}
