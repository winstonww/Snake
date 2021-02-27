package models.game

import models.IDrawable


interface IGame : IDrawable {
    fun onInterval(interval: Double) : Game
    fun onKey(keyCode: Int): Game
    fun onClick(x: Double, y: Double): Game
    fun onWebSocket(s: String): Game
}