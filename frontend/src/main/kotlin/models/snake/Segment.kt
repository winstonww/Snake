package models.snake

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import models.IDrawable
import models.food.IFood
import models.map.Line
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.absoluteValue

@Serializable
sealed class Segment : IDrawable {

    var color: String = "black"
    abstract fun abs(): Double
    abstract fun recede(amount: Double)
    abstract fun advance(amount: Double)
    abstract fun intersect(other: Segment) : Boolean
    abstract fun intersect(other: Line): Boolean
    abstract fun overlap(food: IFood): Boolean
    abstract val startPosition: Pair<Double, Double>

    @Serializable
    sealed class Vertical(
        @SerialName("vX")
        open var x: Double,
        @SerialName("vY1")
        open var y1: Double,
        @SerialName("vY2")
        open var y2: Double,
        var thickness: Double = 5.0,

    ) : Segment() {

        override val startPosition: Pair<Double, Double>
            get() = Pair(x, y1)

        @Serializable
        data class Up(
            override var x: Double,
            override var y1: Double,
            override var y2: Double
        ) : Vertical(x, y1, y2) {
            override fun abs(): Double {
                return y2 - y1
            }

            override fun recede(amount: Double) {
                y2 -= amount
            }

            override fun advance(amount: Double) {
                y1 -= amount
            }
        }

        @Serializable
        data class Down(
            override var x: Double,
            override var y1: Double,
            override var y2: Double
        ) : Vertical(x, y1, y2) {
            override fun abs(): Double {
                return y1 - y2
            }

            override fun recede(amount: Double) {
                y2 += amount
            }

            override fun advance(amount: Double) {
                y1 += amount
            }

        }

        override fun intersect(other: Segment): Boolean {
            if (other is Segment.Vertical) {
                return false
            }

            val s2 = other as Segment.Horizontal
            return (((other.x1 <= x && x <= other.x2) ||
                    (s2.x2 <= x && x <= s2.x1)) &&
                    ((y1 <= s2.y && s2.y <= y2) ||
                            (y2 <= s2.y && s2.y <= y1)))
        }

        override fun intersect(other: Line): Boolean {
            if (other is Line.Vertical) {
                return false
            }

            val s2 = other as Line.Horizontal
            return (((other.x1 <= x && x <= other.x2) ||
                    (s2.x2 <= x && x <= s2.x1)) &&
                    ((y1 <= s2.y && s2.y <= y2) ||
                            (y2 <= s2.y && s2.y <= y1)))
        }



        override fun onDraw(ctx: CanvasRenderingContext2D) {
            ctx.save()
            ctx.lineWidth = thickness
            ctx.strokeStyle = color
            ctx.beginPath()
            ctx.moveTo(x, y1)
            ctx.lineTo(x, y2)
            ctx.closePath()
            ctx.stroke()
            ctx.restore()
        }

        override fun overlap(food: IFood): Boolean {
            return (food.y in minOf(y1, y2)..maxOf(y1,y2)) &&
                    (x - food.x).absoluteValue < food.r
        }
    }

    @Serializable
    sealed class Horizontal(
        @SerialName("hY")
        open var y: Double,
        @SerialName("hX1")
        open var x1: Double,
        @SerialName("hX2")
        open var x2: Double,
        var thickness: Double = 5.0,
    ) : Segment() {

        override val startPosition: Pair<Double, Double>
            get() = Pair(x1, y)

        @Serializable
        data class Left(
            override var y: Double,
            override var x1: Double,
            override var x2: Double
        ) : Horizontal(y, x1, x2) {
            override fun abs(): Double {
                return x2 - x1
            }

            override fun recede(amount: Double) {
                x2 -= amount
            }

            override fun advance(amount: Double) {
                x1 -= amount
            }

        }

        @Serializable
        data class Right(
            override var y: Double,
            override var x1: Double,
            override var x2: Double
        ) : Horizontal(y, x1, x2) {
            override fun abs(): Double {
                return x1 - x2
            }

            override fun recede(amount: Double) {
                x2 += amount
            }

            override fun advance(amount: Double) {
                x1 += amount
            }
        }

         override fun intersect(other: Segment): Boolean {
            if (other is Horizontal) {
                return false
            }

            val s2 = other as Vertical
            return (((other.y1 <= y && y <= other.y2) ||
                    (other.y2 <= y && y <= other.y1)) &&
                    ((x1 <= s2.x && s2.x <= x2) ||
                            (x2 <= s2.x && s2.x <= x1)))

        }

         override fun intersect(other: Line): Boolean {
            if (other is Line.Horizontal) {
                return false
            }

            val s2 = other as Line.Vertical
            return (((other.y1 <= y && y <= other.y2) ||
                    (other.y2 <= y && y <= other.y1)) &&
                    ((x1 <= s2.x && s2.x <= x2) ||
                            (x2 <= s2.x && s2.x <= x1)))

        }

        override fun onDraw(ctx: CanvasRenderingContext2D) {
            ctx.save()
            ctx.strokeStyle = color
            ctx.lineWidth = thickness
            ctx.beginPath()
            ctx.moveTo(x1, y)
            ctx.lineTo(x2, y)
            ctx.closePath()
            ctx.stroke()
            ctx.restore()
        }

        override fun overlap(food: IFood): Boolean {
            return (food.x in minOf(x1, x2)..maxOf(x1,x2)) &&
                    (y - food.y).absoluteValue < food.r
        }

    }

}




