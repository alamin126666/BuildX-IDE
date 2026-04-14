package com.buildx.ide.ui.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buildx.ide.R
import com.buildx.ide.databinding.ActivityCodeEditorBinding
import com.buildx.ide.model.EditorSettings
import com.buildx.ide.model.EditorTheme

class CodeEditorActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCodeEditorBinding
    private var currentFilePath: String = ""
    private var isModified = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodeEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupEditor()
        loadFile()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
    }
    
    private fun setupEditor() {
        // Configure editor settings
        val settings = EditorSettings(
            fontSize = 14,
            tabSize = 4,
            useSpaces = true,
            showLineNumbers = true,
            highlightCurrentLine = true,
            enableAutoComplete = true,
            theme = EditorTheme.DARK,
            wordWrap = false
        )
        
        applyEditorSettings(settings)
        
        // Track modifications
        // binding.codeEditor.addTextChangedListener {
        //     isModified = true
        // }
    }
    
    private fun applyEditorSettings(settings: EditorSettings) {
        // Apply theme
        when (settings.theme) {
            EditorTheme.DARK -> {
                // Apply dark theme colors
            }
            EditorTheme.LIGHT -> {
                // Apply light theme colors
            }
            EditorTheme.MONOKAI -> {
                // Apply monokai theme
            }
            else -> {
                // Default theme
            }
        }
        
        // Apply font size
        // binding.codeEditor.setTextSize(settings.fontSize)
        
        // Apply tab size
        // binding.codeEditor.setTabWidth(settings.tabSize)
        
        // Show/hide line numbers
        // binding.codeEditor.setShowLineNumbers(settings.showLineNumbers)
    }
    
    private fun loadFile() {
        currentFilePath = intent.getStringExtra("file_path") ?: ""
        
        if (currentFilePath.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Load file content
        try {
            val content = readFileContent(currentFilePath)
            // binding.codeEditor.setText(content)
            binding.toolbar.title = currentFilePath.split("/").last()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun readFileContent(filePath: String): String {
        return java.io.File(filePath).readText()
    }
    
    private fun saveFile() {
        try {
            val content = "Editor content" // binding.codeEditor.getText().toString()
            java.io.File(currentFilePath).writeText(content)
            isModified = false
            Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (isModified) {
                    // Show save dialog
                    finish()
                } else {
                    finish()
                }
                true
            }
            R.id.action_save -> {
                saveFile()
                true
            }
            R.id.action_undo -> {
                // binding.codeEditor.undo()
                true
            }
            R.id.action_redo -> {
                // binding.codeEditor.redo()
                true
            }
            R.id.action_find -> {
                // Show find dialog
                true
            }
            R.id.action_replace -> {
                // Show replace dialog
                true
            }
            R.id.action_settings -> {
                // Show editor settings
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onBackPressed() {
        if (isModified) {
            // Show unsaved changes dialog
            android.app.AlertDialog.Builder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Do you want to save?")
                .setPositiveButton("Save") { _, _ ->
                    saveFile()
                    finish()
                }
                .setNegativeButton("Discard") { _, _ ->
                    finish()
                }
                .setNeutralButton("Cancel", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }
}
