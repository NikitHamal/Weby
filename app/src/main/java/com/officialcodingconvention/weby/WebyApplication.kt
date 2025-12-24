package com.officialcodingconvention.weby

import android.app.Application
import android.content.Intent
import com.officialcodingconvention.weby.core.crash.CrashHandler
import com.officialcodingconvention.weby.data.local.database.WebyDatabase
import com.officialcodingconvention.weby.data.local.datastore.PreferencesManager
import com.officialcodingconvention.weby.data.local.filesystem.FileSystemManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class WebyApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var database: WebyDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var fileSystemManager: FileSystemManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        setupCrashHandler()
        initializeDependencies()
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(
            CrashHandler(this, defaultHandler)
        )
    }

    private fun initializeDependencies() {
        database = WebyDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)
        fileSystemManager = FileSystemManager(this)
    }

    companion object {
        @Volatile
        private var instance: WebyApplication? = null

        fun getInstance(): WebyApplication {
            return instance ?: throw IllegalStateException(
                "WebyApplication not initialized"
            )
        }
    }
}
