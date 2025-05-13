package com.example.api.todos.exception

class TodoNotFoundException(id: Long) : RuntimeException("Todo with id $id not found")