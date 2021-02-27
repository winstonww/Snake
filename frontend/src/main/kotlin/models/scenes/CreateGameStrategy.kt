package models.scenes

import models.food.Food
import models.food.FoodFactory
import models.food.IFood
import models.map.Line
import models.map.Map
import models.score.Score
import models.snake.ISnake
import models.snake.Segment
import models.snake.Snake
import org.w3c.dom.WebSocket
import utils.Direction

class CreateGameStrategy(
    private val mapX: Double,
    private val mapY: Double,
    private val mapW: Double,
    private val mapH: Double,
    private val ws: WebSocket) {

    private fun createSnake(): Snake {
        val segments = ArrayDeque<Segment>()
        segments.addFirst(Segment.Horizontal.Right(mapH, 200.0, 100.0))
        return Snake(segments, 0.03, Direction.Right(), "black")
    }

    private fun createOtherSnake(): Snake {
        val segments = ArrayDeque<Segment>()
        segments.addFirst(Segment.Horizontal.Right(mapH - 50, 200.0, 100.0))
        return Snake(segments, 0.03, Direction.Right(), "green")
    }

    fun joinGame(food: IFood): Scene.Game {
        return createGame(snake = createOtherSnake(), other = createSnake(), food = food)
    }

    fun createGame(food: IFood,
                    snake: ISnake = createSnake(),
                   other: ISnake = createOtherSnake()): Scene.Game {

        val score = Score(0, mapX + mapW - 50.0, mapY + mapH + 20.0)

        val map = Map(
            listOf(
                Line.Vertical(mapX, mapY, mapY + mapH),
                Line.Horizontal(mapY + mapH, mapX, mapX + mapW),
                Line.Vertical(mapX + mapW, mapY, mapY + mapH),
                Line.Horizontal(mapY, mapX + mapW, mapX)
            )
        )

        val foodFactory = FoodFactory(
            mapX,
            mapY,
            mapW,
            mapH,
            10.0,
            5.0,
            10.0,
            "red",
            food as Food
        )
        return Scene.Game(snake, other, map, foodFactory, score, ws)
    }
}