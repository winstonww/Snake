package models.scenes

import models.IDrawable
import utils.Direction

interface IScene: IDrawable {
    fun onInterval(interval: Double) : Scene
    fun onDirection(direction: Direction): Scene
    fun onClick(x: Double, y: Double): Scene
    fun onDestroy()
    fun onWebSocket(s: String): Scene
}