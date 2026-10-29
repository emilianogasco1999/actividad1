package com.example.myapplication

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.myapplication.ui.navigation.AppNavigation

@Composable
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}
