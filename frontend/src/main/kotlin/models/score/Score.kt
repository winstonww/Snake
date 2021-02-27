package models.score

import org.w3c.dom.CanvasRenderingContext2D

class Score(override var score: Int, val x: Double, val y: Double): IScore {

    override fun onDraw(ctx: CanvasRenderingContext2D) {
        ctx.save()
        ctx.fillText("Score: ${score}", x, y)
        ctx.restore()
    }
}