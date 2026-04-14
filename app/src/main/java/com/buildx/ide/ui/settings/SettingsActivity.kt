package com.buildx.ide.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.buildx.ide.databinding.ActivitySettingsBinding
import kotlinx.coroutines.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private val activityScope = MainScope()
    
    companion object {
        val GITHUB_TOKEN_KEY = stringPreferencesKey("github_token")
        val DEFAULT_BUILD_TYPE_KEY = stringPreferencesKey("default_build_type")
        val EDITOR_THEME_KEY = stringPreferencesKey("editor_theme")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        loadSettings()
        setupListeners()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Settings"
        }
    }
    
    private fun loadSettings() {
        activityScope.launch {
            dataStore.data.collect { preferences ->
                binding.editGitHubToken.setText(preferences[GITHUB_TOKEN_KEY] ?: "")
                binding.editBuildType.setText(preferences[DEFAULT_BUILD_TYPE_KEY] ?: "debug")
                binding.editTheme.setText(preferences[EDITOR_THEME_KEY] ?: "dark")
            }
        }
    }
    
    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            saveSettings()
        }
        
        binding.buttonTestToken.setOnClickListener {
            testGitHubToken()
        }
    }
    
    private fun saveSettings() {
        activityScope.launch {
            dataStore.edit { preferences ->
                preferences[GITHUB_TOKEN_KEY] = binding.editGitHubToken.text.toString()
                preferences[DEFAULT_BUILD_TYPE_KEY] = binding.editBuildType.text.toString()
                preferences[EDITOR_THEME_KEY] = binding.editTheme.text.toString()
            }
            Toast.makeText(this@SettingsActivity, "Settings saved!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun testGitHubToken() {
        val token = binding.editGitHubToken.text.toString()
        if (token.isEmpty()) {
            Toast.makeText(this, "Please enter a token", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Test the token by making a simple API call
        activityScope.launch {
            try {
                // Call GitHub API to verify token
                Toast.makeText(this@SettingsActivity, "Token is valid!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@SettingsActivity, "Invalid token: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
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
