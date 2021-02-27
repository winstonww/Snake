package canvas

import models.game.Game
import org.w3c.dom.CanvasRenderingContext2D

class Canvas(val ctx: CanvasRenderingContext2D) : ICanvas {
    override fun draw(game: Game) {
        clear()
        game.onDraw(ctx)
    }

    fun clear() {
        ctx.clearRect(
            0.toDouble(),
            0.toDouble(),
            ctx.canvas.width.toDouble(),
            ctx.canvas.height.toDouble()
        )
    }
}
