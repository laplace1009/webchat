package com.example.webchat.model

import org.springframework.web.socket.WebSocketSession

data class UserSession(var name: String, val session: WebSocketSession)