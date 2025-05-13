package com.example.api.users.controller

import com.example.api.config.jwt.JwtTokenProvider
import com.example.api.users.dto.LoginRequest
import com.example.api.users.dto.SignUpRequest
import com.example.api.users.dto.UpdateUserRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private val objectMapper = ObjectMapper()

    private val email = "test@example.com"
    private val password = "password123"
    private val name = "테스트 사용자"

    private var userId: Long = 1L
    private lateinit var accessToken: String

    @BeforeEach
    fun setUp() {
        // 회원 가입
        val signUpRequest = SignUpRequest(email, password, name)
        mockMvc.post("/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signUpRequest)
        }

        // 로그인
        val loginRequest = LoginRequest(email, password)
        val result = mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andReturn()

        val json = result.response.contentAsString
        accessToken = objectMapper.readTree(json).get("access_token").asText()
        assertNotNull(accessToken)

        // 사용자 ID 추출
        val meResult = mockMvc.get("/users/me") {
            header("Authorization", "Bearer $accessToken")
        }.andReturn()
        userId = objectMapper.readTree(meResult.response.contentAsString).get("id").asLong()
    }

    @Test
    fun `회원 가입`() {
        val request = SignUpRequest("signup@example.com", "password", "회원가입테스트")
        mockMvc.post("/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `로그인`() {
        val request = LoginRequest(email, password)
        mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.access_token") { exists() }
        }
    }

    @Test
    fun `내 정보 조회`() {
        mockMvc.get("/users/me") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(userId) }
            jsonPath("$.email") { value(email) }
        }
    }

    @Test
    fun `내 정보 수정`() {
        val newName = "수정된 이름"
        val updateRequest = UpdateUserRequest(null, newName)

        mockMvc.put("/users/me") {
            header("Authorization", "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.nickname") { value(newName) }
        }
    }

    @Test
    fun `회원 탈퇴`() {
        mockMvc.delete("/users/me") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `JWT 없이 유저 정보 조회 시 401 반환`() {
        mockMvc.get("/users/me") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `유효하지 않은 ID의 JWT로 유저 정보 조회 시 404 반환`() {
        // 존재하지 않는 ID로 토큰 생성
        val invalidUserId = 99999L
        val invalidToken = jwtTokenProvider.createToken(invalidUserId)

        mockMvc.get("/users/me") {
            header("Authorization", "Bearer $invalidToken")
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `회원가입부터 탈퇴까지 JWT 기반 전체 흐름 통합 테스트`() {
        // 1. 회원 가입
        val signupEmail = "flow@example.com"
        val signupPassword = "flowpass123"
        val signupName = "통합 사용자"
        val signUpRequest = SignUpRequest(signupEmail, signupPassword, signupName)

        mockMvc.post("/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signUpRequest)
        }.andExpect {
            status { isOk() }
        }

        // 2. 로그인
        val loginRequest = LoginRequest(signupEmail, signupPassword)
        val loginResult = mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.access_token") { exists() }
        }.andReturn()

        val token = objectMapper.readTree(loginResult.response.contentAsString).get("access_token").asText()
        assertNotNull(token)

        // 3. 내 정보 조회
        val meResult = mockMvc.get("/users/me") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$.email") { value(signupEmail) }
            jsonPath("$.nickname") { value(signupName) }
        }.andReturn()

        val userId = objectMapper.readTree(meResult.response.contentAsString).get("id").asLong()

        // 4. 내 정보 수정
        val updatedName = "수정된 사용자"
        val updateRequest = UpdateUserRequest(null, updatedName)

        mockMvc.put("/users/me") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.nickname") { value(updatedName) }
        }

        // 5. 회원 탈퇴
        mockMvc.delete("/users/me") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isNoContent() }
        }

        // 6. 탈퇴 후 다시 내 정보 조회 시 404
        mockMvc.get("/users/me") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isNotFound() }
        }
    }

}
