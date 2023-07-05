package com.copixelate.nav

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.compareRoute(route: String): Boolean =
    currentBackStackEntry?.destination?.route == route

fun NavBackStackEntry?.compareTopLevelRoute(route: String) =
    this?.destination?.hierarchy?.any { navDest ->
        navDest.route == route
    } == true

fun NavController.navigateTopLevel(route: String) =
    navigate(route) {
        popUpTo(graph.findStartDestination().id)
        launchSingleTop = true
    }
