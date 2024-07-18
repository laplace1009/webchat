package com.example.webchat.domain

data class Message(
    val name: String,
    val message: String,
    val changedName: String,
    val userList: List<String>
)