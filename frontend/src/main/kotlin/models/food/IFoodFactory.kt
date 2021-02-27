package models.food

import models.snake.ISnake


interface IFoodFactory {
    fun create(snake1: ISnake, snake2: ISnake): Food
}