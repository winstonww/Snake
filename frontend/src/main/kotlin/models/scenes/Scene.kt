package models.scenes


import kotlinx.browser.document
import kotlinx.serialization.json.Json
import models.food.Food
import models.map.IMap
import models.food.IFoodFactory
import models.score.IScore
import models.snake.ISnake
import models.snake.Snake
import org.w3c.dom.*
import utils.Direction
import kotlin.random.Random

sealed class Scene: IScene {

    override fun onDirection(direction: Direction): Scene {
        return this
    }

    override fun onClick(x: Double, y: Double): Scene {
        return this
    }

    override fun onInterval(interval: Double): Scene {
        return this
    }

    override fun onDestroy() {
    }

    override fun onWebSocket(s: String): Scene {
        return this
    }

    class Start(val ws: WebSocket, val mapX: Double = 20.0, val mapY: Double = 20.0, val mapW: Double, val mapH: Double): Scene() {

        val snakeTitleX = mapX + mapW / 2
        val snakeTitleY = mapY + mapH / 2

        val thisRoomIdX = snakeTitleX + 3
        val thisRoomIdY = snakeTitleY + 20

        val createGameX = snakeTitleX
        val createGameY = snakeTitleY + 50
        val createGameW = 95.0
        val createGameH = 20.0

        val roomIdX = createGameX + 10
        val roomIdY = createGameY + 50

        val inputX = createGameX - 15
        val inputY = createGameY + 80

        val joinGameX = createGameX + 5
        val joinGameY = createGameY + 100
        val joinGameW = 80.0
        val joinGameH = 20.0

        var input: HTMLInputElement? = null

        val roomId: String by lazy {  Random.nextInt(0, 999).toString() }

        val createGameStrategy = CreateGameStrategy(mapX, mapY, mapW, mapH, ws)

        override fun onDraw(ctx: CanvasRenderingContext2D) {
            ctx.save()
            ctx.font = "bold ${30}px Georgia, serif"

            ctx.fillText(
                "Snake",
                snakeTitleX,
                snakeTitleY)


            ctx.font = "${11}px Georgia, serif"
            ctx.fillText(
                "This Room ID: ${roomId}",
                thisRoomIdX,
                thisRoomIdY
            )

            ctx.font = "${15}px Georgia, serif"

            ctx.textBaseline = CanvasTextBaseline.Companion.TOP

            ctx.strokeRect(
                createGameX,
                createGameY,
                createGameW,
                createGameH
            )

            ctx.fillText(
                "Create Game",
                createGameX + 5.0,
                createGameY + 5.0)

            ctx.strokeRect(
                joinGameX,
                joinGameY,
                joinGameW,
                joinGameH
            )

            ctx.fillText("Room ID:", roomIdX + 5.0, roomIdY)

            if (input == null) {
                input = document.createElement("input") as HTMLInputElement
                input!!.setAttribute("id", "in1")
                input!!.style.position = "fixed"
                input!!.style.left = inputX.toString() + "px"
                input!!.style.top = inputY.toString() + "px"
                document.body?.appendChild(input!!)
            }

            ctx.fillText(
                "Join Game",
                joinGameX + 5.0,
                joinGameY + 5.0)

            ctx.restore()
        }

        override fun onClick(x: Double, y: Double): Scene {
            if (x in (createGameX..createGameX+createGameW) &&
                    y in (createGameY..createGameY+createGameH)) {
                ws.send("CREATE ${roomId} ")
                return WaitOpponent(mapX, mapY, mapW, mapH, roomId, ws)
            }
            if (x in (joinGameX..joinGameX+joinGameW) &&
                y in (joinGameY..joinGameY+joinGameH)) {
                console.log(input!!.value)
                ws.send("JOIN ${input?.value} ")
                return this
            }
            return this
        }

        override fun onWebSocket(s: String): Scene {
            val ls = s.split(" ")
            if (ls[0] == "JOINECHO") {
                val food = Json.decodeFromString(Food.serializer(),ls[2])
                val gameScene =  createGameStrategy.joinGame(food)
                return gameScene
            }
            return this
        }

        override fun onDestroy() {
            input!!.remove()
            input = null
        }
    }

    class WaitOpponent(val mapX: Double,
                       mapY: Double,
                       mapW: Double,
                       mapH: Double,
                       val roomId: String,
                       ws: WebSocket): Scene() {

        val titleX = mapX + mapW / 2
        val titleY = mapY + mapH / 2

        val createGameStrategy = CreateGameStrategy(mapX, mapY, mapW, mapH, ws)

        override fun onDraw(ctx: CanvasRenderingContext2D) {
            ctx.save()
            ctx.font = "${15}px Georgia, serif"
            ctx.textAlign = CanvasTextAlign.Companion.CENTER
            ctx.fillText(
                "Waiting for Opponent...",
                titleX,
                titleY
            )
            ctx.restore()
        }

        override fun onWebSocket(s: String): Scene {
            val ls = s.split(" ")
            if (ls[0] == "JOINECHO") {
                val food = Json.decodeFromString(Food.serializer(),ls[2])
                return createGameStrategy.createGame(food = food)
            }
            return this
        }
    }

    class Game(var snake: ISnake,
               var other: ISnake,
               val gameMap: IMap,
               val foodFactory: IFoodFactory,
               val score: IScore,
               val ws: WebSocket): Scene() {

        var food = foodFactory.create(snake, other)


        override fun onInterval(interval: Double): Scene {
            snake.update(interval)
            other.update(interval)

            if (isGameOver()) {
                return End(score.score)
            }
            if (snake.ate(food)) {
                score.score += 100
                food = foodFactory.create(snake, other)
                ws.send("UPDATE food ${Json.encodeToString(Food.serializer(), food)}")
            }
            return this
        }

        override fun onDraw(ctx: CanvasRenderingContext2D) {
            snake.onDraw(ctx)
            other.onDraw(ctx)
            gameMap.onDraw(ctx)
            food.onDraw(ctx)
            score.onDraw(ctx)
        }

        override fun onDirection(direction: Direction): Scene {
            snake.turn(direction)
            ws.send("UPDATE snake ${Json.encodeToString(Snake.serializer(), snake as Snake)}")
            return this
        }

        private fun isGameOver() : Boolean {
            if(snake.bitItself()){
                return true
            }
            if(other.bitItself()){
                return true
            }
            if (snake.hit(gameMap)) {
                return true
            }
            if (other.hit(gameMap)) {
                return true
            }
            return false
        }

        override fun onWebSocket(s: String): Scene {
            val ls = s.split(' ')
            if (ls[0] != "UPDATEECHO") {
                return this
            }
            when(ls[1]) {
                "snake" -> {other = Json.decodeFromString(Snake.serializer(), ls[2])}
                "food" -> {
                    food = Json.decodeFromString(Food.serializer(), ls[2])
                    score.score += 100
                }
                else -> {}
            }
            return this
        }
    }

    class End(val score: Int): Scene() {

        override fun onDraw(ctx: CanvasRenderingContext2D) {
            ctx.save()
            ctx.font = "bold ${30}px Georgia, serif"
            ctx.textAlign = CanvasTextAlign.Companion.CENTER
            ctx.fillText(
                "Game Over",
                ctx.canvas.width.toDouble() / 4,
                ctx.canvas.height.toDouble() / 4)
            ctx.font = "${15}px Georgia, serif"
            ctx.textAlign = CanvasTextAlign.Companion.CENTER
            ctx.fillText(
                "Final score: ${score}",
                ctx.canvas.width.toDouble() / 4,
                ctx.canvas.height.toDouble() / 4 + 30)
            ctx.restore()
        }

    }

}

