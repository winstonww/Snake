package models.map

import models.IDrawable

interface IMap: IDrawable {
    var borders: List<Line>
}