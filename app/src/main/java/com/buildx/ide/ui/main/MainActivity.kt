package com.buildx.ide.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.buildx.ide.R
import com.buildx.ide.databinding.ActivityMainBinding
import com.buildx.ide.model.BuildOutput
import com.buildx.ide.model.BuildStatus
import com.buildx.ide.repository.GitHubRepository
import com.buildx.ide.ui.adapter.RecentProjectsAdapter
import com.buildx.ide.ui.build.BuildOutputActivity
import com.buildx.ide.ui.editor.CodeEditorActivity
import com.buildx.ide.ui.settings.SettingsActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val githubRepository = GitHubRepository()
    private val mainScope = MainScope()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupNavigationDrawer()
        setupClickListeners()
        setupRecyclerView()
        observeBuildState()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }
    
    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.nav_github_token -> {
                    // Show GitHub token dialog
                    true
                }
                R.id.nav_about -> {
                    // Show about dialog
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.cardNewProject.setOnClickListener {
            Toast.makeText(this, "New Project - Coming Soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardOpenProject.setOnClickListener {
            Toast.makeText(this, "Open Project - Coming Soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardCloneRepo.setOnClickListener {
            Toast.makeText(this, "Clone Repository - Coming Soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardBuild.setOnClickListener {
            triggerGitHubBuild()
        }
        
        binding.fabBuildStatus.setOnClickListener {
            startActivity(Intent(this, BuildOutputActivity::class.java))
        }
    }
    
    private fun setupRecyclerView() {
        binding.recyclerViewRecentProjects.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecentProjectsAdapter { project ->
                // Open project
                val intent = Intent(this@MainActivity, CodeEditorActivity::class.java)
                intent.putExtra("project_path", project.path)
                startActivity(intent)
            }
        }
        
        // Load recent projects from database
        loadRecentProjects()
    }
    
    private fun observeBuildState() {
        mainScope.launch {
            githubRepository.isBuilding.collectLatest { isBuilding ->
                if (isBuilding) {
                    binding.fabBuildStatus.show()
                } else {
                    binding.fabBuildStatus.hide()
                }
            }
        }
    }
    
    private fun triggerGitHubBuild() {
        mainScope.launch {
            try {
                githubRepository.setBuildingState(true)
                
                val buildOutput = BuildOutput(
                    id = UUID.randomUUID().toString(),
                    status = BuildStatus.RUNNING
                )
                githubRepository.addBuildOutput(buildOutput)
                
                // Trigger workflow
                val result = githubRepository.triggerBuild(
                    token = "YOUR_GITHUB_TOKEN", // Get from settings
                    owner = "owner",
                    repo = "repo",
                    workflowId = "build.yml",
                    inputs = mapOf(
                        "project_type" to "android",
                        "build_type" to "debug"
                    )
                )
                
                result.fold(
                    {
                        Toast.makeText(this@MainActivity, "Build triggered successfully!", Toast.LENGTH_SHORT).show()
                        // Update build output
                        githubRepository.updateBuildOutput(
                            buildOutput.id,
                            buildOutput.copy(status = BuildStatus.SUCCESS)
                        )
                    },
                    { error ->
                        Toast.makeText(this@MainActivity, "Build failed: ${error.message}", Toast.LENGTH_LONG).show()
                        // Update build output
                        githubRepository.updateBuildOutput(
                            buildOutput.id,
                            buildOutput.copy(
                                status = BuildStatus.FAILED,
                                errors = listOf(error.message ?: "Unknown error")
                            )
                        )
                    }
                )
            } finally {
                githubRepository.setBuildingState(false)
            }
        }
    }
    
    private fun loadRecentProjects() {
        // Load from Room database
        // For now, using dummy data
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }
}
