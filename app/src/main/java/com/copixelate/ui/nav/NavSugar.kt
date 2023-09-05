package com.copixelate.ui.nav

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.navigate(navInfo: NavInfo.SubScreen): Unit =
    navigate(route = navInfo.route){
        launchSingleTop = true
        restoreState = true
    }

fun NavController.navigateTopLevel(navInfo: NavInfo.Screen): Unit =
    navigate(navInfo.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

fun NavController.compareRoute(navInfo: NavInfo.Screen): Boolean =
    currentBackStackEntry?.destination?.route == navInfo.route

fun NavBackStackEntry?.compareTopLevelRoute(navInfo: NavInfo.Screen): Boolean =
    this?.destination?.hierarchy?.any { navDest ->
        navDest.route == navInfo.route
    } == true
