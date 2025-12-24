package com.officialcodingconvention.weby.core.crash

import android.content.Context
import android.content.Intent
import android.os.Process
import com.officialcodingconvention.weby.CrashActivity
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class CrashHandler(
    private val context: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val crashLog = buildCrashLog(thread, throwable)
            launchCrashActivity(crashLog)
        } catch (e: Exception) {
            defaultHandler?.uncaughtException(thread, throwable)
        }

        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    private fun buildCrashLog(thread: Thread, throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)

        printWriter.println("=== WEBY CRASH REPORT ===")
        printWriter.println()
        printWriter.println("Thread: ${thread.name} (ID: ${thread.id})")
        printWriter.println("Time: ${System.currentTimeMillis()}")
        printWriter.println()
        printWriter.println("=== EXCEPTION ===")
        throwable.printStackTrace(printWriter)
        printWriter.println()
        printWriter.println("=== DEVICE INFO ===")
        printWriter.println("Brand: ${android.os.Build.BRAND}")
        printWriter.println("Model: ${android.os.Build.MODEL}")
        printWriter.println("Device: ${android.os.Build.DEVICE}")
        printWriter.println("Android Version: ${android.os.Build.VERSION.RELEASE}")
        printWriter.println("SDK: ${android.os.Build.VERSION.SDK_INT}")
        printWriter.println()
        printWriter.println("=== APP INFO ===")
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            printWriter.println("Version Name: ${packageInfo.versionName}")
            printWriter.println("Version Code: ${packageInfo.longVersionCode}")
            printWriter.println("Package: ${context.packageName}")
        } catch (e: Exception) {
            printWriter.println("Could not retrieve app info: ${e.message}")
        }
        printWriter.println()
        printWriter.println("=== MEMORY INFO ===")
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        printWriter.println("Used Memory: ${usedMemory}MB")
        printWriter.println("Max Memory: ${maxMemory}MB")

        printWriter.flush()
        return stringWriter.toString()
    }

    private fun launchCrashActivity(crashLog: String) {
        val intent = Intent(context, CrashActivity::class.java).apply {
            putExtra(CrashActivity.EXTRA_CRASH_LOG, crashLog)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
    }
}
