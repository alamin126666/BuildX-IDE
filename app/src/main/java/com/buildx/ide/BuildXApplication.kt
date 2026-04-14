package com.buildx.ide

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.buildx.ide.repository.GitHubRepository

class BuildXApplication : Application() {

    companion object {
        const val BUILD_CHANNEL_ID = "build_notifications"
        const val GENERAL_CHANNEL_ID = "general_notifications"

        lateinit var instance: BuildXApplication
            private set
    }

    val githubRepository by lazy { GitHubRepository() }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val buildChannel = NotificationChannel(
                BUILD_CHANNEL_ID,
                "Build Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for build status updates"
                enableLights(true)
                enableVibration(true)
            }

            val generalChannel = NotificationChannel(
                GENERAL_CHANNEL_ID,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
            }

            notificationManager.createNotificationChannel(buildChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }
}
