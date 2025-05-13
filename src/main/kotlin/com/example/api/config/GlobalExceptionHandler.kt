package com.example.api.config

import com.example.api.todos.exception.TodoNotFoundException
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException) = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)

    @ExceptionHandler(SecurityException::class)
    fun handleUnauthorized(e: SecurityException) = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)

    @ExceptionHandler(TodoNotFoundException::class)
    fun handleTodoNotFound(ex: TodoNotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ex.message ?: "할 일을 찾을 수 없습니다")
    }
}
