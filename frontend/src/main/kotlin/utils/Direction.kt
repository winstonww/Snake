package utils

import kotlinx.serialization.Serializable

@Serializable
sealed class Direction {
    abstract fun orthogonal(other: Direction): Boolean
    abstract fun clockwise(): Direction

    @Serializable
    class Up() : Direction() {
        override fun orthogonal(other: Direction) = other is Left || other is Right
        override fun clockwise(): Direction = Direction.Right()
    }

    @Serializable
    class Down() : Direction() {
        override fun orthogonal(other: Direction) = other is Left || other is Right
        override fun clockwise(): Direction = Direction.Left()
    }

    @Serializable
    class Left() : Direction() {
        override fun orthogonal(other: Direction) = other is Up || other is Down
        override fun clockwise(): Direction = Direction.Up()
    }

    @Serializable
    class Right() : Direction() {
        override fun orthogonal(other: Direction) = other is Up || other is Down
        override fun clockwise(): Direction = Direction.Down()
    }
}
