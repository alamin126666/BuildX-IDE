package com.buildx.ide

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import com.buildx.ide.repository.GitHubRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class BuildXApplication : Application(), Configuration.Provider {
    
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
        
        // Build notifications channel
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
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
