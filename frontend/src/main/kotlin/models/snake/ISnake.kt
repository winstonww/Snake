package models.snake

import models.food.Food
import models.food.IFood
import models.map.IMap
import org.w3c.dom.CanvasRenderingContext2D
import utils.Direction

interface ISnake {
    fun update(deltaTime: Double)
    fun turn(newDirection: Direction)
    fun onDraw(ctx: CanvasRenderingContext2D)
    fun bitItself() : Boolean
    fun hit(gameMap: IMap) : Boolean
    fun ate(food: IFood?): Boolean
    fun overlap(food: IFood?): Boolean
}