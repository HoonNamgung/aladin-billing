package com.example.api.todos.service

import com.example.api.todos.dto.CreateTodoRequest
import com.example.api.todos.dto.TodoResponse
import com.example.api.todos.dto.UpdateTodoRequest
import com.example.api.todos.model.Todo
import com.example.api.todos.repository.TodoRepository
import org.springframework.stereotype.Service
import com.example.api.todos.exception.TodoNotFoundException
import org.springframework.transaction.annotation.Transactional

@Service
class TodoService(
    private val todoRepository: TodoRepository
) {
    fun create(userId: Long, request: CreateTodoRequest): TodoResponse {
        val todo = Todo(userId = userId, title = request.title)
        return todoRepository.save(todo).toResponse()
    }

    fun getAll(userId: Long): List<TodoResponse> {
        return todoRepository.findAllByUserId(userId).map { it.toResponse() }
    }

    fun getOne(userId: Long, id: Long): TodoResponse {
        val todo = getByUser(userId, id)
        return todo.toResponse()
    }

    @Transactional
    fun update(userId: Long, id: Long, request: UpdateTodoRequest): TodoResponse {
        val todo = getByUser(userId, id)
        request.title?.let { todo.title = it }
        request.completed?.let { todo.completed = it }
        return todo.toResponse()
    }

    fun deleteOne(userId: Long, id: Long) {
        val todo = getByUser(userId, id)
        todoRepository.delete(todo)
    }

    fun searchTodos(userId: Long, isDone: Boolean?, keyword: String?): List<TodoResponse> {
        return todoRepository.searchTodos(userId, isDone, keyword)
            .map { it.toResponse() }
    }

    private fun getByUser(userId: Long, id: Long): Todo {
        return todoRepository.findById(id)
            .filter { it.userId == userId }
            .orElseThrow { TodoNotFoundException(id) }
    }

    private fun Todo.toResponse() = TodoResponse(id, title, completed)
}
