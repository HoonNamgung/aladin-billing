package com.example.api.todos.service

import com.example.api.todos.dto.CreateTodoRequest
import com.example.api.todos.exception.TodoNotFoundException
import com.example.api.todos.model.Todo
import com.example.api.todos.repository.TodoRepository
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional

@ExtendWith(MockKExtension::class)
class TodoServiceTest {

    private val todoRepository = mockk<TodoRepository>()
    private val todoService = TodoService(todoRepository)

    @Test
    fun `할 일 생성`() {
        val userId = 1L
        val request = CreateTodoRequest(title = "테스트 할일")
        val saved = Todo(id = 1L, userId = userId, title = request.title)

        every { todoRepository.save(any()) } returns saved

        val result = todoService.create(userId, request)

        Assertions.assertThat(result.title).isEqualTo("테스트 할일")
    }

    @Test
    fun `할 일 단건 조회 실패 - ID 불일치`() {
        val userId = 1L
        val id = 2L
        val todo = Todo(id = id, userId = 999L, title = "다른 사용자")

        every { todoRepository.findById(id) } returns Optional.of(todo)

        Assertions.assertThatThrownBy { todoService.getOne(userId, id) }
            .isInstanceOf(TodoNotFoundException::class.java)
    }

    @Test
    fun `할 일 삭제`() {
        val userId = 1L
        val id = 1L
        val todo = Todo(id = id, userId = userId, title = "삭제할 일")

        every { todoRepository.findById(id) } returns Optional.of(todo)
        every { todoRepository.delete(todo) } returns Unit

        todoService.deleteOne(userId, id)

        verify { todoRepository.delete(todo) }
    }
}