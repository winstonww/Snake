package models.food

import models.IDrawable

interface IFood : IDrawable {
    val x: Double
    val y: Double
    val r: Double
    val color: String
    val amount: Double
    val sensitivity : Double
    fun contains(x: Double, y: Double): Boolean
}
