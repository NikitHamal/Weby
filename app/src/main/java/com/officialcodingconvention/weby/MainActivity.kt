package com.officialcodingconvention.weby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.officialcodingconvention.weby.core.navigation.WebyNavHost
import com.officialcodingconvention.weby.core.theme.WebyTheme
import com.officialcodingconvention.weby.data.local.datastore.ThemeMode
import com.officialcodingconvention.weby.data.local.datastore.UserPreferences

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesManager = WebyApplication.getInstance().preferencesManager

        setContent {
            val preferences by preferencesManager.userPreferences.collectAsState(
                initial = UserPreferences()
            )

            val darkTheme = when (preferences.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            WebyTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    WebyNavHost(navController = navController)
                }
            }
        }
    }
}
