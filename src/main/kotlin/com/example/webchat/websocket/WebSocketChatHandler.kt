package com.example.webchat.websocket

import com.example.webchat.domain.Message
import com.example.webchat.model.UserSession
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import kotlin.math.abs
import kotlin.random.Random

class WebSocketChatHandler: TextWebSocketHandler() {
    companion object {
        val sessions: HashMap<String, UserSession> = HashMap()
        val objectMapper = jacksonObjectMapper()

        fun getUserList(): List<String> {
            return sessions.map { it.value.name }
        }

        fun setName(name: String, session: WebSocketSession) {
            val newSession = UserSession(name, session)
            sessions.remove(session.id)
            sessions.set(session.id, newSession)
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val name = abs(Random.nextLong()).toString()
        session.let {
            setName(name, session)
            val message = Message("", "", name, emptyList())
            val json = objectMapper.writeValueAsString(message)
            it.sendMessage(TextMessage(json))
        }
        val json = objectMapper.writeValueAsString(Message("", "", "", getUserList()))
        broadcastMessage(TextMessage(json))
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session.id)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload = message.payload
        val receivedMessage: Message = objectMapper.readValue(payload)
        val changedName = receivedMessage.changedName
        if (changedName.isNotEmpty()) {
            if (sessions.filter { it.value.name.equals(changedName) }.isNotEmpty()) {
                return
            }
            setName(receivedMessage.changedName, session)
            session.sendMessage(message)
            val json = objectMapper.writeValueAsString(Message("", "", "", getUserList()))
            broadcastMessage(TextMessage(json))
        } else {
            broadcastMessage(message)
        }
    }

    private fun broadcastMessage(message: TextMessage) {
        sessions.values.forEach{ it.session.sendMessage(message) }
    }

}