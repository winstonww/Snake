package models.food

import kotlinx.serialization.Serializable
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI
import kotlin.math.hypot

@Serializable
data class Food(
    override val x: Double,
    override val y: Double,
    override val r: Double,
    override val color: String,
    override val amount: Double = 15.0,
    override val sensitivity : Double = 1.5
): IFood {
    override fun onDraw(ctx: CanvasRenderingContext2D) {
        ctx.save()
        ctx.beginPath()
        ctx.arc(x, y, r, 0.0, PI*2)
        ctx.fillStyle = color
        ctx.fill()
        ctx.closePath()
        ctx.restore()
    }

    override fun contains(otherX: Double, otherY: Double): Boolean {
        return hypot((otherX - x), (otherY - y)) <= sensitivity * r
    }
}
