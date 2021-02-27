package models.snake

import models.food.Food
import models.food.IFood
import models.map.IMap
import org.w3c.dom.CanvasRenderingContext2D
import utils.Direction

import kotlinx.serialization.*


@Serializable
class Snake(
    @Serializable(with = SegmentArrayDequeSerializer::class) var segments: ArrayDeque<Segment>,
    val velocity: Double,
    var direction: Direction,
    val color: String
) : ISnake {

    val head: Segment
        get() = segments.first()

    override fun update(deltaTime: Double) {
        segments.updateHead(velocity, deltaTime)
        segments.updateTail(velocity, deltaTime)
    }

    override fun turn(newDirection: Direction) {
        if (!newDirection.orthogonal(direction)) return
        segments.addFirst(
            when (newDirection) {
                is Direction.Up -> {
                    val old = head as Segment.Horizontal
                    Segment.Vertical.Up(x = old.x1, y1 = old.y, y2 = old.y)
                }
                is Direction.Down -> {
                    val old = head as Segment.Horizontal
                    Segment.Vertical.Down(x = old.x1, y1 = old.y, y2 = old.y)
                }
                is Direction.Left -> {
                    val old = head as Segment.Vertical
                    Segment.Horizontal.Left(y = old.y1, x1 = old.x, x2 = old.x)
                }
                is Direction.Right -> {
                    val old = head as Segment.Vertical
                    Segment.Horizontal.Right(y = old.y1, x1 = old.x, x2 = old.x)
                }
            }
        )
        direction = newDirection
    }

    private fun ArrayDeque<Segment>.updateHead(velocity: Double, deltaTime: Double) {
        first().apply {
            advance(velocity * deltaTime)
        }
    }

    private fun ArrayDeque<Segment>.updateTail(velocity: Double, deltaTime: Double) {
        var tail: Segment = last()
        tail.recede(velocity * deltaTime)
        var length = tail.abs()
        while (length <= 0) {
            removeLast()
            tail = last()
            tail.recede(-length)
            length = tail.abs()
        }
    }

    private fun ArrayDeque<Segment>.afterSecond(): List<Segment> {
        return filterIndexed { index, _ -> index > 1 }
    }

    override fun onDraw(ctx: CanvasRenderingContext2D) {
        segments.forEach { seg ->
            seg.color = this.color
            seg.onDraw(ctx)
        }
    }

    override fun toString(): String {
        return segments.map {
            when(it) {
                is Segment.Vertical -> {"X: ${it.x} Start: ${it.y1} End: ${it.y2}"}
                is Segment.Horizontal -> {"Y: ${it.y} Start: ${it.x1} End: ${it.x2}"}
            }

        }.joinToString(separator = "\n")
    }

    override fun bitItself(): Boolean {
        return segments.afterSecond().any { head.intersect(it) }
    }

    override fun hit(gameMap: IMap): Boolean {
        return gameMap.borders.any { head.intersect(it) }
    }

    override fun ate(food: IFood?): Boolean {
        if (food == null) {
            return true
        }
        val eaten =  head.startPosition.run {
            food.contains(first, second)
        }
        if (eaten) {
            head.advance(food.amount)
        }
        return eaten
    }

    override fun overlap(food: IFood?): Boolean {
        if (food == null) return false
        return segments.any {
            it.overlap(food)
        }
    }


}
