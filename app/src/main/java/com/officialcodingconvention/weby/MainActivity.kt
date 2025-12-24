package com.officialcodingconvention.weby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.officialcodingconvention.weby.core.navigation.WebyNavHost
import com.officialcodingconvention.weby.core.theme.WebyTheme
import com.officialcodingconvention.weby.data.local.datastore.ThemeMode

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesManager = WebyApplication.getInstance().preferencesManager

        setContent {
            val preferences by preferencesManager.userPreferences.collectAsState(
                initial = null
            )

            val darkTheme = when (preferences?.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                else -> isSystemInDarkTheme()
            }

            WebyTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    WebyNavHost(navController = navController)
                }
            }
        }
    }
}
