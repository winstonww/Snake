package controller

import canvas.Canvas
import models.scenes.Scene
import kotlinx.browser.document
import kotlinx.browser.window
import models.game.Game
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.WebSocket
import utils.Keys

object Controller : IController {

    lateinit var canvas: Canvas
    lateinit var context: CanvasRenderingContext2D
    lateinit var game: Game
    lateinit var ws: WebSocket
    private const val TIME_INTERVAL = 20.0


    override fun initialize() {

        ws = WebSocket("ws://localhost:5000/myws/echo")

        val htmlCanvas = document.querySelector("canvas") as HTMLCanvasElement
        context = htmlCanvas.getContext("2d") as CanvasRenderingContext2D
        canvas = Canvas(context)
        context.canvas.width = window.innerWidth
        context.canvas.height = window.innerHeight
        context.canvas.tabIndex = 1

        val gameScene = Scene.Start(ws, 20.0, 20.0, context.canvas.width/2.0, context.canvas.height/2.0)
        game = Game(gameScene)
    }

    override fun run() {

        window.setInterval(
            {
                game.onInterval(TIME_INTERVAL)
            }, TIME_INTERVAL.toInt()
        )

        window.setInterval(
            {
                canvas.draw(game)
            }, TIME_INTERVAL.toInt()
        )


        window.onkeydown = {
            if (setOf(Keys.Left.code,
                    Keys.Right.code,
                    Keys.Up.code,
                    Keys.Down.code).contains(it.keyCode)) {
                it.preventDefault()
            }
            game.onKey(it.keyCode)
        }

        window.onclick = {
            game.onClick(it.x, it.y)
        }

        ws.onmessage = {
            console.log("Messaged")
            console.log(it.data)
            game.onWebSocket(it.data.toString())
        }

    }

}