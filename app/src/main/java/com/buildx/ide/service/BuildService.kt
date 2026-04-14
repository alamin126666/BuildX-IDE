package com.buildx.ide.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.buildx.ide.BuildXApplication
import com.buildx.ide.R
import com.buildx.ide.model.BuildOutput
import com.buildx.ide.model.BuildStatus
import com.buildx.ide.repository.GitHubRepository
import com.buildx.ide.ui.build.BuildOutputActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class BuildService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val githubRepository = GitHubRepository()
    private var currentBuild: BuildOutput? = null
    
    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START_BUILD = "ACTION_START_BUILD"
        const val ACTION_CANCEL_BUILD = "ACTION_CANCEL_BUILD"
        
        const val EXTRA_TOKEN = "extra_token"
        const val EXTRA_OWNER = "extra_owner"
        const val EXTRA_REPO = "extra_repo"
        const val EXTRA_WORKFLOW = "extra_workflow"
        const val EXTRA_INPUTS = "extra_inputs"
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_BUILD -> {
                val token = intent.getStringExtra(EXTRA_TOKEN) ?: ""
                val owner = intent.getStringExtra(EXTRA_OWNER) ?: ""
                val repo = intent.getStringExtra(EXTRA_REPO) ?: ""
                val workflow = intent.getStringExtra(EXTRA_WORKFLOW) ?: ""
                val inputs = intent.getSerializableExtra(EXTRA_INPUTS) as? Map<String, String> ?: emptyMap()
                
                startForeground(NOTIFICATION_ID, createBuildNotification("Build started", "Running build..."))
                
                serviceScope.launch {
                    triggerBuild(token, owner, repo, workflow, inputs)
                }
            }
            ACTION_CANCEL_BUILD -> {
                stopSelf()
            }
        }
        return START_STICKY
    }
    
    private suspend fun triggerBuild(
        token: String,
        owner: String,
        repo: String,
        workflow: String,
        inputs: Map<String, String>
    ) {
        try {
            githubRepository.setBuildingState(true)
            
            val buildOutput = BuildOutput(
                id = java.util.UUID.randomUUID().toString(),
                status = BuildStatus.RUNNING
            )
            currentBuild = buildOutput
            githubRepository.addBuildOutput(buildOutput)
            
            updateNotification("Build in progress", "Running $workflow...")
            
            val result = githubRepository.triggerBuild(token, owner, repo, workflow, inputs)
            
            result.fold(
                {
                    currentBuild = currentBuild?.copy(status = BuildStatus.SUCCESS)
                    updateNotification("Build successful", "Build completed successfully!")
                    githubRepository.updateBuildOutput(currentBuild!!.id, currentBuild!!)
                },
                { error ->
                    currentBuild = currentBuild?.copy(
                        status = BuildStatus.FAILED,
                        errors = listOf(error.message ?: "Unknown error")
                    )
                    updateNotification("Build failed", error.message ?: "Unknown error")
                    githubRepository.updateBuildOutput(currentBuild!!.id, currentBuild!!)
                }
            )
        } finally {
            githubRepository.setBuildingState(false)
            stopSelf()
        }
    }
    
    private fun createBuildNotification(title: String, message: String): Notification {
        val intent = Intent(this, BuildOutputActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val cancelIntent = Intent(this, BuildService::class.java).apply {
            action = ACTION_CANCEL_BUILD
        }
        val cancelPendingIntent = PendingIntent.getService(
            this, 1, cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, BuildXApplication.BUILD_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_build)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", cancelPendingIntent)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()
    }
    
    private fun updateNotification(title: String, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createBuildNotification(title, message))
    }
    
    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
