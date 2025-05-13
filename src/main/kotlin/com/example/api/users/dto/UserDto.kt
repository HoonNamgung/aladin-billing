package com.example.api.users.dto

data class SignUpRequest(val email: String, val password: String, val nickname: String)
data class LoginRequest(val email: String, val password: String)
data class UpdateUserRequest(val password: String?, val nickname: String?)
data class UserResponse(val id: Long, val email: String, val nickname: String)
