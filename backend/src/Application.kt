package com.winstonww

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import java.net.http.WebSocket
import java.time.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    var roomToclients = hashMapOf<String, DefaultWebSocketServerSession>()
    var createToJoin = hashMapOf<DefaultWebSocketServerSession, DefaultWebSocketServerSession>()

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        webSocket("/myws/echo") {
            send(Frame.Text("Hi from server"))
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    val text = frame.readText().split(" ")
                    when(text[0]) {
                        "CREATE" -> {
                            roomToclients[text[1]] = this
                        }
                        "JOIN" -> {
                            if (roomToclients.contains(text[1])) {
                                createToJoin[roomToclients[text[1]]!!] = this
                                createToJoin[this] = roomToclients[text[1]]!!
                                val other = roomToclients[text[1]]
                                if (other != null) {
                                    // TODO: Modify hard coded location of initial food
                                    send(Frame.Text("JOINECHO food {\"x\":328,\"y\":183,\"r\":6,\"color\":\"red\"}"))
                                    other.send(Frame.Text("JOINECHO food {\"x\":328,\"y\":183,\"r\":6,\"color\":\"red\"}"))
                                }
                            }
                        }
                        "UPDATE" -> {
                            val other = createToJoin[this]
                            other?.send(
                                Frame.Text(
                                    "UPDATEECHO " + text.filterIndexed{ index, s -> index >= 1 }.joinToString(" ")
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


