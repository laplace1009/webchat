package com.example.webchat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebchatApplication

fun main(args: Array<String>) {
    runApplication<WebchatApplication>(*args)
}