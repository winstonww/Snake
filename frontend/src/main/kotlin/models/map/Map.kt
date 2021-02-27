package models.map

import org.w3c.dom.CanvasRenderingContext2D

class Map(override var borders: List<Line>) : IMap {
    override fun onDraw(ctx: CanvasRenderingContext2D) {
        borders.forEach {
            it.onDraw(ctx)
        }



    }
}