package com.buildx.ide.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buildx.ide.R
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

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerViewRecentProjects: RecyclerView
    private val githubRepository = GitHubRepository()
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        recyclerViewRecentProjects = findViewById(R.id.recyclerViewRecentProjects)

        setupToolbar()
        setupNavigationDrawer()
        setupClickListeners()
        setupRecyclerView()
        observeBuildState()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun setupNavigationDrawer() {
        findViewById<com.google.android.material.navigation.NavigationView>(R.id.navigationView)
            .setNavigationItemSelectedListener { menuItem ->
                drawerLayout.closeDrawer(GravityCompat.START)
                when (menuItem.itemId) {
                    R.id.nav_settings -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
    }

    private fun setupClickListeners() {
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardNewProject)
            .setOnClickListener { Toast.makeText(this, "New Project - Coming Soon!", Toast.LENGTH_SHORT).show() }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardOpenProject)
            .setOnClickListener { Toast.makeText(this, "Open Project - Coming Soon!", Toast.LENGTH_SHORT).show() }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardCloneRepo)
            .setOnClickListener { Toast.makeText(this, "Clone Repository - Coming Soon!", Toast.LENGTH_SHORT).show() }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardBuild)
            .setOnClickListener { triggerGitHubBuild() }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabBuildStatus)
            .setOnClickListener { startActivity(Intent(this, BuildOutputActivity::class.java)) }
    }

    private fun setupRecyclerView() {
        recyclerViewRecentProjects.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecentProjectsAdapter { project ->
                val intent = Intent(this@MainActivity, CodeEditorActivity::class.java)
                intent.putExtra("project_path", project.path)
                startActivity(intent)
            }
        }
    }

    private fun observeBuildState() {
        mainScope.launch {
            githubRepository.isBuilding.collectLatest { isBuilding ->
                val fab = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabBuildStatus)
                if (isBuilding) fab.show() else fab.hide()
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

                val result = githubRepository.triggerBuild(
                    token = "YOUR_GITHUB_TOKEN",
                    owner = "owner",
                    repo = "repo",
                    workflowId = "build.yml",
                    inputs = mapOf("project_type" to "android", "build_type" to "debug")
                )

                result.fold(
                    {
                        Toast.makeText(this@MainActivity, "Build triggered!", Toast.LENGTH_SHORT).show()
                        githubRepository.updateBuildOutput(buildOutput.id, buildOutput.copy(status = BuildStatus.SUCCESS))
                    },
                    { error ->
                        Toast.makeText(this@MainActivity, "Build failed: ${error.message}", Toast.LENGTH_LONG).show()
                        githubRepository.updateBuildOutput(buildOutput.id, buildOutput.copy(status = BuildStatus.FAILED, errors = listOf(error.message ?: "Unknown")))
                    }
                )
            } finally {
                githubRepository.setBuildingState(false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
                else drawerLayout.openDrawer(GravityCompat.START)
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
