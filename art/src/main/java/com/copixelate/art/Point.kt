package com.copixelate.art

import kotlin.math.floor
import kotlin.math.min

data class Point(var x: Int = 0, var y: Int = x) {
    operator fun div(i: Int) = Point(x / i, y / i)
    operator fun div(f: Float) = PointF(x / f, y / f)
    operator fun div(p: Point) = PointF(x * 1f / p.x, y * 1f / p.y)

    operator fun plusAssign(i: Int) {
        x += i; y += i
    }

    val area get() = x * y
    fun contains(p: PointF): Boolean = (p.x > 0 && p.x < x && p.y > 0 && p.y < y)
    fun toMinSquare() = Point(min(x, y))
}

data class PointF(var x: Float = 0f, var y: Float = x) {
    operator fun times(f: Float) = PointF(f * x, f * y)
    operator fun times(p: PointF) = PointF(x * p.x, y * p.y)
    operator fun times(p: Point) = PointF(x * p.x, y * p.y)
    operator fun plus(p: PointF) = PointF(x + p.x, y + p.y)
    operator fun div(p: Point) = PointF(x / p.x, y / p.y)

    fun toIndex(bounds: Point): Int = (floor(y) * bounds.x + floor(x)).toInt()
    fun asUnitToIndex(bounds: Point): Int = (this * bounds).run { toIndex(bounds) }
    fun isUnit() = Point(1).contains(this)
}

fun List<PointF>.toIndexes(bounds: Point): IntArray = run {
    toMutableList().apply {
        retainAll { point -> bounds.contains(point) }
    }.run { IntArray(size) { this[it].toIndex(bounds) } }
}
