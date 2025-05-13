package com.example.api.users.service

import com.example.api.users.dto.LoginRequest
import com.example.api.users.dto.SignUpRequest
import com.example.api.users.dto.UpdateUserRequest
import com.example.api.users.model.User
import com.example.api.users.repository.UserRepository
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = spyk(BCryptPasswordEncoder())
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository)
    }

    @Test
    fun `회원 가입 성공`() {
        // given
        val request = SignUpRequest(email = "test@example.com", password = "password", nickname = "닉네임")
        every { userRepository.findByEmail(request.email) } returns null
        every { userRepository.save(any()) } answers { firstArg() }

        // when
        val response = userService.signUp(request)

        // then
        assertThat(response.email).isEqualTo(request.email)
        assertThat(response.nickname).isEqualTo(request.nickname)
    }

    @Test
    fun `회원 가입 실패 - 이메일 중복`() {
        val request = SignUpRequest(email = "test@example.com", password = "password", nickname = "닉네임")
        every { userRepository.findByEmail(request.email) } returns User(email = request.email, password = "xxx", nickname = "existing")

        assertThatThrownBy { userService.signUp(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Email already exists")
    }

    @Test
    fun `로그인 성공`() {
        val rawPassword = "password"
        val encodedPassword = passwordEncoder.encode(rawPassword)
        val request = LoginRequest(email = "test@example.com", password = rawPassword)
        val user = User(email = request.email, password = encodedPassword, nickname = "닉네임")

        every { userRepository.findByEmail(request.email) } returns user

        val result = userService.login(request)

        assertThat(result.email).isEqualTo(request.email)
    }

    @Test
    fun `로그인 실패 - 유저 없음`() {
        val request = LoginRequest(email = "notfound@example.com", password = "password")
        every { userRepository.findByEmail(request.email) } returns null

        assertThatThrownBy { userService.login(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User not found")
    }

    @Test
    fun `로그인 실패 - 비밀번호 불일치`() {
        val request = LoginRequest(email = "test@example.com", password = "wrong")
        val user = User(email = request.email, password = passwordEncoder.encode("correct"), nickname = "닉네임")

        every { userRepository.findByEmail(request.email) } returns user

        assertThatThrownBy { userService.login(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid password")
    }

    @Test
    fun `내 정보 조회`() {
        val user = User(id = 1L, email = "user@example.com", password = "xxx", nickname = "닉네임")
        every { userRepository.findById(1L) } returns Optional.of(user)

        val response = userService.getMyInfo(1L)

        assertThat(response.email).isEqualTo(user.email)
        assertThat(response.nickname).isEqualTo(user.nickname)
    }

    @Test
    fun `회원 정보 수정`() {
        val user = User(id = 1L, email = "user@example.com", password = "xxx", nickname = "oldNick")
        val updateRequest = UpdateUserRequest(password = "newpass", nickname = "newNick")

        every { userRepository.findById(1L) } returns Optional.of(user)
        every { userRepository.save(any()) } answers { firstArg() }

        val updated = userService.update(1L, updateRequest)

        assertThat(updated.nickname).isEqualTo("newNick")
        assertThat(passwordEncoder.matches("newpass", user.password)).isTrue()
    }

    @Test
    fun `회원 탈퇴`() {
        every { userRepository.deleteById(1L) } just Runs

        userService.delete(1L)

        verify { userRepository.deleteById(1L) }
    }
}
