package com.example.api.todos.controller

import com.example.api.todos.dto.*
import com.example.api.todos.service.TodoService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoController(
    private val todoService: TodoService
) {

    @PostMapping
    fun create(@AuthenticationPrincipal userId: Long, @RequestBody request: CreateTodoRequest) =
        ResponseEntity.ok(todoService.create(userId, request))

    @GetMapping
    fun getAll(@AuthenticationPrincipal userId: Long) =
        ResponseEntity.ok(todoService.getAll(userId))

    @GetMapping("/{id}")
    fun getOne(@AuthenticationPrincipal userId: Long, @PathVariable id: Long) =
        ResponseEntity.ok(todoService.getOne(userId, id))

    @PutMapping("/{id}")
    fun update(@AuthenticationPrincipal userId: Long, @PathVariable id: Long, @RequestBody request: UpdateTodoRequest) =
        ResponseEntity.ok(todoService.update(userId, id, request))

    @DeleteMapping("/{id}")
    fun deleteOne(@AuthenticationPrincipal userId: Long, @PathVariable id: Long): ResponseEntity<Void> {
        todoService.deleteOne(userId, id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/search")
    fun searchTodos(
        @AuthenticationPrincipal userId: Long,
        @RequestParam(required = false) isDone: Boolean?,
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<List<TodoResponse>> {
        return ResponseEntity.ok(todoService.searchTodos(userId, isDone, keyword))
    }
}
