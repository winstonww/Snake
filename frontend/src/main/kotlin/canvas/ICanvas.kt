package canvas

import models.game.Game


interface ICanvas {
    fun draw(game: Game)
}