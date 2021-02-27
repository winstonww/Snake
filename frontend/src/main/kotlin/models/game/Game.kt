package models.game

import models.scenes.Scene
import org.w3c.dom.CanvasRenderingContext2D
import utils.Direction
import utils.Keys
import kotlin.properties.Delegates


class Game(s: Scene) : IGame{

    var scene: Scene by Delegates.observable(s) { _, old, new ->
        if (old != new) {
            old.onDestroy()
        }
    }
    override fun onDraw(ctx: CanvasRenderingContext2D) {
        scene.onDraw(ctx)
    }

    override fun onInterval(interval: Double) : Game {
        scene = scene.onInterval(interval)
        return this
    }

    override fun onKey(keyCode: Int): Game {
        if (!setOf<Int>(Keys.Left.code, Keys.Down.code, Keys.Up.code, Keys.Right.code).contains(keyCode)) {
            return this
        }
        when(keyCode) {
            Keys.Left.code -> {scene = scene.onDirection(Direction.Left())}
            Keys.Right.code -> {scene = scene.onDirection(Direction.Right())}
            Keys.Up.code -> {scene = scene.onDirection(Direction.Up())}
            Keys.Down.code -> {scene = scene.onDirection(Direction.Down())}
        }
        return this
    }

    override fun onClick(x: Double, y:Double): Game {
        scene = scene.onClick(x, y)
        return this
    }

    override fun onWebSocket(s: String): Game {
        console.log("hereee")
        scene = scene.onWebSocket(s)
        return this
    }
}
