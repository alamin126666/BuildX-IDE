package com.buildx.ide.ui.buildoutput

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buildx.ide.R
import com.buildx.ide.model.BuildOutput
import com.buildx.ide.model.BuildStatus
import com.buildx.ide.repository.GitHubRepository
import com.buildx.ide.ui.adapter.BuildHistoryAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class BuildOutputActivity : AppCompatActivity() {
    
    private lateinit var textViewSummary: TextView
    private lateinit var recyclerViewBuilds: RecyclerView
    private val githubRepository = GitHubRepository()
    private val activityScope = MainScope()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_output)
        
        textViewSummary = findViewById(R.id.textViewSummary)
        recyclerViewBuilds = findViewById(R.id.recyclerViewBuilds)
        
        setupToolbar()
        setupRecyclerView()
        observeBuilds()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Build Output"
        }
    }
    
    private fun setupRecyclerView() {
        recyclerViewBuilds.apply {
            layoutManager = LinearLayoutManager(this@BuildOutputActivity)
            adapter = BuildHistoryAdapter { build ->
                showBuildDetails(build)
            }
        }
    }
    
    private fun observeBuilds() {
        activityScope.launch {
            githubRepository.buildOutputs.collectLatest { builds ->
                (recyclerViewBuilds.adapter as? BuildHistoryAdapter)?.submitList(builds)
                updateBuildSummary(builds)
            }
        }
    }
    
    private fun updateBuildSummary(builds: List<BuildOutput>) {
        val totalBuilds = builds.size
        val successfulBuilds = builds.count { it.status == BuildStatus.SUCCESS }
        val failedBuilds = builds.count { it.status == BuildStatus.FAILED }
        val runningBuilds = builds.count { it.status == BuildStatus.RUNNING }
        
        textViewSummary.text = """
            Total Builds: $totalBuilds
            Successful: $successfulBuilds
            Failed: $failedBuilds
            Running: $runningBuilds
        """.trimIndent()
    }
    
    private fun showBuildDetails(build: BuildOutput) {
        // Navigate to detailed build output
        // For now, show a dialog
        android.app.AlertDialog.Builder(this)
            .setTitle("Build ${build.id.take(8)}")
            .setMessage("""
                Status: ${build.status}
                Start Time: ${build.startTime}
                End Time: ${build.endTime ?: "N/A"}
                Errors: ${build.errors.size}
                Artifacts: ${build.artifacts.size}
            """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }
}
