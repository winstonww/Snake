package models

import org.w3c.dom.CanvasRenderingContext2D

interface IDrawable {
    fun onDraw(ctx: CanvasRenderingContext2D)
}