package models.food


import models.snake.ISnake
import kotlin.random.Random


class FoodFactory(private val canvasX: Double,
                  private val canvasY: Double,
                  private val canvasW: Double,
                  private val canvasH: Double,
                  private val padding: Double,
                  private val foodMinSize: Double,
                  private val foodMaxSize: Double,
                  private val color: String,
                  private val initial: Food): IFoodFactory {
    private var initialized = false
    override fun create(snake1: ISnake, snake2: ISnake): Food {
        if (!initialized) {
            initialized = true
            return initial
        }
        lateinit var food: Food
        do {
            food = Food(
                (Random.nextDouble(canvasX + padding, canvasX + canvasW - padding)),
                (Random.nextDouble(canvasY + padding, canvasY + canvasH - padding)),
                Random.nextDouble(foodMinSize, foodMaxSize),
                color
            )
        } while(snake1.overlap(food) || snake2.overlap(food))
        return food
    }
}