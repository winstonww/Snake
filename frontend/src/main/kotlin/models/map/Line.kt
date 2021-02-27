package models.map

import models.IDrawable
import org.w3c.dom.CanvasRenderingContext2D

sealed class Line : IDrawable{
    class Horizontal(val y: Double, val x1: Double, val x2: Double): Line() {
        override fun onDraw(ctx: CanvasRenderingContext2D) {
            ctx.beginPath()
            ctx.moveTo(x1, y)
            ctx.lineTo(x2, y)
            ctx.closePath()
            ctx.stroke()
        }
    }
    class Vertical(val x: Double, val y1: Double, val y2: Double): Line() {
        override fun onDraw(ctx: CanvasRenderingContext2D) {
            ctx.beginPath()
            ctx.moveTo(x, y1)
            ctx.lineTo(x, y2)
            ctx.closePath()
            ctx.stroke()
        }
    }
}