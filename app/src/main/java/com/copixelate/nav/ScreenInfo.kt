package com.copixelate.nav

sealed class ScreenInfo(val route: String) {
    object Art: ScreenInfo(route = "art")
    object Overview: ScreenInfo(route = "messages")
}
