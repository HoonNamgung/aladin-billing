package com.example.api.todos.dto


data class CreateTodoRequest(
    val title: String
)

data class UpdateTodoRequest(
    val title: String?,
    val completed: Boolean?
)

data class TodoResponse(
    val id: Long,
    val title: String,
    val completed: Boolean
)