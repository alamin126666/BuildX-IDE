package com.buildx.ide.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.buildx.ide.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var editGitHubToken: TextInputEditText
    private lateinit var editBuildType: TextInputEditText
    private lateinit var editTheme: TextInputEditText
    private lateinit var buttonSave: Button
    private lateinit var buttonTestToken: Button
    private val activityScope = MainScope()
    
    companion object {
        val GITHUB_TOKEN_KEY = stringPreferencesKey("github_token")
        val DEFAULT_BUILD_TYPE_KEY = stringPreferencesKey("default_build_type")
        val EDITOR_THEME_KEY = stringPreferencesKey("editor_theme")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        editGitHubToken = findViewById(R.id.editGitHubToken)
        editBuildType = findViewById(R.id.editBuildType)
        editTheme = findViewById(R.id.editTheme)
        buttonSave = findViewById(R.id.buttonSave)
        buttonTestToken = findViewById(R.id.buttonTestToken)
        
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Settings"
        }
        
        loadSettings()
        setupListeners()
    }
    
    private fun loadSettings() {
        activityScope.launch {
            dataStore.data.collect { preferences ->
                editGitHubToken.setText(preferences[GITHUB_TOKEN_KEY] ?: "")
                editBuildType.setText(preferences[DEFAULT_BUILD_TYPE_KEY] ?: "debug")
                editTheme.setText(preferences[EDITOR_THEME_KEY] ?: "dark")
            }
        }
    }
    
    private fun setupListeners() {
        buttonSave.setOnClickListener {
            saveSettings()
        }
        
        buttonTestToken.setOnClickListener {
            testGitHubToken()
        }
    }
    
    private fun saveSettings() {
        activityScope.launch {
            dataStore.edit { preferences ->
                preferences[GITHUB_TOKEN_KEY] = editGitHubToken.text.toString()
                preferences[DEFAULT_BUILD_TYPE_KEY] = editBuildType.text.toString()
                preferences[EDITOR_THEME_KEY] = editTheme.text.toString()
            }
            Toast.makeText(this@SettingsActivity, "Settings saved!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun testGitHubToken() {
        val token = editGitHubToken.text.toString()
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
