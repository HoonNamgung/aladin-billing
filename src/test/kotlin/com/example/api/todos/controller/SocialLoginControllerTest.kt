package com.example.api.todos.controller

import com.example.api.users.infra.google.client.GoogleHttpClient
import com.example.api.users.dto.GoogleUserInfoResponse
import com.example.api.users.model.User
import com.example.api.users.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class SocialLoginControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var googleHttpClient: GoogleHttpClient

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `should create a new user and return JWT when Google callback is called with new user`() {
        // given
        val fakeCode = "mock-code"
        val mockAccessToken = "mock-access-token"
        val mockEmail = "googleuser@example.com"
        val mockName = "구글 유저"

        `when`(googleHttpClient.getAccessToken(fakeCode)).thenReturn(mockAccessToken)
        `when`(googleHttpClient.getUserInfo(mockAccessToken)).thenReturn(
            GoogleUserInfoResponse("1", mockEmail, true, mockName, mockName, mockName, "pic")
        )

        // when & then
        mockMvc.get("/users/social/callback") {
            param("code", fakeCode)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.access_token") { exists() }
        }

        // verify user was saved
        val savedUser = userRepository.findByEmail(mockEmail)
        assert(savedUser != null) { "유저가 저장되지 않았습니다." }
        assert(savedUser!!.nickname == mockName)
    }

    @Test
    fun `should return JWT without creating user when Google callback is called with existing user`() {
        // given
        val existingEmail = "existinguser@example.com"
        val existingUser = userRepository.save(User(email = existingEmail, password = "", nickname = "기존유저"))
        val fakeCode = "mock-code"
        val mockAccessToken = "mock-access-token"

        `when`(googleHttpClient.getAccessToken(fakeCode)).thenReturn(mockAccessToken)
        `when`(googleHttpClient.getUserInfo(mockAccessToken)).thenReturn(
            GoogleUserInfoResponse("1", existingEmail, true, "기존유저", "기존유저", "기존유저", "pic")
        )

        // when & then
        mockMvc.get("/users/social/callback") {
            param("code", fakeCode)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.access_token") { exists() }
        }
    }
}
