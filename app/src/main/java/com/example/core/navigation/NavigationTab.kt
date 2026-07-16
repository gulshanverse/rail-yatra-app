package com.example.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationTab(
    val id: String,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)
