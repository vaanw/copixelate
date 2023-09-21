package com.copixelate.art

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ArtSpaceTest {

    @ParameterizedTest
    @MethodSource("variousFloats")
    fun updateDrawing_WithVariousValues_DoesNotCrash(f: Float) {
        ArtSpace().updateDrawing(unitPosition = PointF(f))
    }

    companion object {
        @JvmStatic
        fun variousFloats() = listOf(
            -100000000f,
            -1f,
            -0.01f,
            -0.00000001f,
            0f,
            0.00000001f,
            0.01f,
            0.99f,
            0.9999999f,
            1f,
            1.0000001f,
            1.01f,
            100000000f
        )
    }

}
