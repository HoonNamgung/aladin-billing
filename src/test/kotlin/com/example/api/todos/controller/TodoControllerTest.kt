package com.example.api.todos.controller

import com.example.api.config.jwt.JwtTokenProvider
import com.example.api.todos.dto.CreateTodoRequest
import com.example.api.todos.dto.UpdateTodoRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@ExtendWith(MockKExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private val userId = 1L

    @Test
    fun `할 일 생성`() {
        val request = CreateTodoRequest("할 일 제목")
        val token = jwtTokenProvider.createToken(userId)

        mockMvc.post("/todos") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = ObjectMapper().writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `할 일 전체 조회`() {
        val token = jwtTokenProvider.createToken(userId)

        mockMvc.get("/todos") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$") { isArray() }
        }
    }

    @Test
    fun `조건 기반 할 일 조회`() {
        val query = "제목"
        val token = jwtTokenProvider.createToken(userId)

        mockMvc.get("/todos/search") {
            header("Authorization", "Bearer $token")
            param("query", query)
        }.andExpect {
            status { isOk() }
            jsonPath("$") { isArray() }
        }
    }

    @Test
    fun `할 일 단건 조회`() {
        // 먼저 생성
        val createdId = createTodoAndGetId("조회 테스트")
        val token = jwtTokenProvider.createToken(userId)

        mockMvc.get("/todos/$createdId") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("조회 테스트") }
        }
    }

    @Test
    fun `할 일 수정`() {
        val createdId = createTodoAndGetId("수정 전 제목")
        val updateRequest = UpdateTodoRequest("수정된 제목", completed = true)
        val token = jwtTokenProvider.createToken(userId)


        mockMvc.put("/todos/$createdId") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = ObjectMapper().writeValueAsString(updateRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("수정된 제목") }
            jsonPath("$.completed") { value(true) }
        }
    }

    @Test
    fun `할 일 수정 대상이 없는 경우`() {
        val updateRequest = UpdateTodoRequest("수정된 제목", completed = true)
        val token = jwtTokenProvider.createToken(userId)

        mockMvc.put("/todos/100") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = ObjectMapper().writeValueAsString(updateRequest)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `할 일 삭제`() {
        val createdId = createTodoAndGetId("삭제 테스트")
        val token = jwtTokenProvider.createToken(userId)

        mockMvc.delete("/todos/$createdId") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isNoContent() }
        }
    }

    private fun createTodoAndGetId(title: String): Long {
        val request = CreateTodoRequest(title)
        val token = jwtTokenProvider.createToken(userId)

        val mvcResult = mockMvc.post("/todos") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = ObjectMapper().writeValueAsString(request)
        }.andReturn()

        val json = mvcResult.response.contentAsString
        return ObjectMapper().readTree(json).get("id").asLong()
    }

    @Test
    fun `할 일 생성부터 삭제까지 통합 흐름 테스트`() {
        val token = jwtTokenProvider.createToken(userId)

        // 1. 할 일 생성
        val createRequest = CreateTodoRequest("통합 테스트 제목")
        val createResult = mockMvc.post("/todos") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = ObjectMapper().writeValueAsString(createRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("통합 테스트 제목") }
            jsonPath("$.completed") { value(false) }
        }.andReturn()

        val createdId = ObjectMapper().readTree(createResult.response.contentAsString).get("id").asLong()

        // 2. 할 일 목록 전체 조회
        mockMvc.get("/todos") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$") { isArray() }
            jsonPath("$[?(@.id == $createdId)].title") { value("통합 테스트 제목") }
        }

        // 3. 할 일 수정
        val updateRequest = UpdateTodoRequest("통합 테스트 수정", completed = true)
        mockMvc.put("/todos/$createdId") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = ObjectMapper().writeValueAsString(updateRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("통합 테스트 수정") }
            jsonPath("$.completed") { value(true) }
        }

        // 4. 할 일 삭제
        mockMvc.delete("/todos/$createdId") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isNoContent() }
        }

        // 5. 삭제 후 조회 시 Not Found
        mockMvc.get("/todos/$createdId") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isNotFound() }
        }
    }
}