package com.example.api.todos.repository

import com.example.api.todos.model.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TodoRepository : JpaRepository<Todo, Long> {
    fun findAllByUserId(userId: Long): List<Todo>

    @Query("""
        SELECT t FROM Todo t
        WHERE t.userId = :userId
        AND (:isDone IS NULL OR t.completed = :isDone)
        AND (:keyword IS NULL OR t.title LIKE %:keyword%)
    """)
    fun searchTodos(
        userId: Long,
        isDone: Boolean?,
        keyword: String?
    ): List<Todo>
}