package com.buildx.ide.repository

import com.buildx.ide.api.APIClient
import com.buildx.ide.api.WorkflowRun
import com.buildx.ide.api.WorkflowDispatchRequest
import com.buildx.ide.api.RepositoryContent
import com.buildx.ide.model.BuildOutput
import com.buildx.ide.model.BuildStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import android.util.Base64

class GitHubRepository {
    
    private val _buildOutputs = MutableStateFlow<List<BuildOutput>>(emptyList())
    val buildOutputs: StateFlow<List<BuildOutput>> = _buildOutputs.asStateFlow()
    
    private val _isBuilding = MutableStateFlow(false)
    val isBuilding: StateFlow<Boolean> = _isBuilding.asStateFlow()
    
    suspend fun triggerBuild(
        token: String,
        owner: String,
        repo: String,
        workflowId: String,
        inputs: Map<String, String> = emptyMap()
    ): Result<Unit> {
        return try {
            val api = APIClient.getApi(token)
            val request = WorkflowDispatchRequest(ref = "main", inputs = inputs)
            val response = api.triggerWorkflow(owner, repo, workflowId, "Bearer $token", request)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to trigger build: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWorkflowRuns(
        token: String,
        owner: String,
        repo: String,
        status: String? = null
    ): Result<List<WorkflowRun>> {
        return try {
            val api = APIClient.getApi(token)
            val response = api.listWorkflowRuns(owner, repo, "Bearer $token", status)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.workflow_runs)
            } else {
                Result.failure(Exception("Failed to get workflow runs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWorkflowRun(
        token: String,
        owner: String,
        repo: String,
        runId: Long
    ): Result<WorkflowRun> {
        return try {
            val api = APIClient.getApi(token)
            val response = api.getWorkflowRun(owner, repo, runId, "Bearer $token")
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get workflow run: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveWorkflow(
        token: String,
        owner: String,
        repo: String,
        workflowName: String,
        content: String,
        branch: String = "main"
    ): Result<Unit> {
        return try {
            val api = APIClient.getApi(token)
            val path = ".github/workflows/$workflowName.yml"
            
            // Check if file exists to get SHA
            val existingFile = try {
                api.getRepositoryContent(owner, repo, path, "Bearer $token")
            } catch (e: Exception) {
                null
            }
            
            val sha = if (existingFile != null && existingFile.isSuccessful) {
                existingFile.body()?.sha
            } else null
            
            val encodedContent = Base64.encodeToString(content.toByteArray(), Base64.NO_WRAP)
            val request = com.buildx.ide.api.CreateFileRequest(
                message = "Update workflow: $workflowName",
                content = encodedContent,
                branch = branch,
                sha = sha
            )
            
            val response = api.createOrUpdateFile(owner, repo, path, "Bearer $token", request)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to save workflow: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWorkflowContent(
        token: String,
        owner: String,
        repo: String,
        workflowName: String
    ): Result<String> {
        return try {
            val api = APIClient.getApi(token)
            val path = ".github/workflows/$workflowName.yml"
            val response = api.getRepositoryContent(owner, repo, path, "Bearer $token")
            
            if (response.isSuccessful && response.body()?.content != null) {
                val decodedContent = String(
                    Base64.decode(response.body()!!.content, Base64.NO_WRAP)
                )
                Result.success(decodedContent)
            } else {
                Result.failure(Exception("Workflow not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun addBuildOutput(output: BuildOutput) {
        _buildOutputs.value = _buildOutputs.value + output
    }
    
    fun updateBuildOutput(id: String, output: BuildOutput) {
        _buildOutputs.value = _buildOutputs.value.map {
            if (it.id == id) output else it
        }
    }
    
    fun setBuildingState(building: Boolean) {
        _isBuilding.value = building
    }
}
